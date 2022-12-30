#include <iostream>
#include <fstream>
#include <cmath>
#include <C:\Program Files (x86)\Microsoft MPI\MPI_SDK\Include\mpi.h>
//#include <mpi.h>

#include "operations.h"


constexpr double epsilon = 1e-6;
constexpr size_t counterLimit = 10000;
constexpr size_t N = 2500;

bool writeResultToFile(Matrix &matrix, const std::string &name) {
	std::ofstream out(name, std::ios::out | std::ios::binary | std::ios::trunc);
	if (out.fail()) {
		std::cerr << "Couldn't open output file" << std::endl;
		return false;
	}
	try {
		out.write(reinterpret_cast<const char *>(matrix.matrix_), matrix.cols_ * matrix.rows_ * sizeof(DATA_TYPE));
	} catch (std::exception &e) {
		out.close();
		std::cerr << "Caught an exception: " << e.what() << std::endl;
		return false;
	}
	out.close();
	return true;
}

int main(int argc, char **argv) {
	if (argc < 3) {
		std::cerr << "Gimme the files" << std::endl;
		return -1;
	}

	MPI_Init(&argc, &argv);
	int rank = 0;
	int numProcesses = 0;
	double start = 0;
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	if (0 == rank) {
		start = MPI_Wtime();
	}
	MPI_Comm_size(MPI_COMM_WORLD, &numProcesses);
	Matrix A(N, N);
	Matrix B(N, 1);
	if (0 == rank) {
		std::ifstream file(argv[1]);
		if (file.fail()) {
			std::cerr << "Failed to open " << argv[1] <<std::endl;
			return -2;
		}
		try {
			file.read(reinterpret_cast<char *>(A.matrix_), N * N * sizeof(DATA_TYPE));
		} catch (std::exception &e) {
			file.close();
			std::cerr << "Caught an exception: " << e.what() << std::endl;
			return -3;
		}
		file.close();

		file.open(argv[2]);

		if (file.fail()) {
			std::cerr << "Failed to open " << argv[2] <<std::endl;
			return -4;
		}
		try {
			file.read(reinterpret_cast<char *>(B.matrix_), N * sizeof(DATA_TYPE));
		} catch (std::exception &e) {
			file.close();
			std::cerr << "Caught an exception: " << e.what() << std::endl;
			return -5;
		}
		file.close();
	}
	MPI_Bcast(B.matrix_, N, MPI_DOUBLE, 0, MPI_COMM_WORLD);

	int *sizes = new int[numProcesses];
	int *displs = new int[numProcesses];

	{
		int base = N / numProcesses;
		int remain = N % numProcesses;
		int offset = 0;
		for (int i = 0; i < numProcesses; ++i) {
			displs[i] = offset;
			sizes[i] = base * N;
			offset += base * N;
			if (remain) {
				sizes[i] += N;
				offset += N;
				--remain;
			}
		}
	}

	//vectors(not local)
	Matrix xN(N, 1);
	Matrix rN(A.rows_, 1);
	Matrix zN(rN.rows_, 1);
	Matrix Ax(A.rows_, 1);
	Matrix AZn(A.rows_, 1);
	Matrix alphaZn(zN.rows_, 1);
	Matrix alphaAZn(A.rows_, 1);

	//local A
	Matrix localA(sizes[rank] / N, N);
	//local vectors
	Matrix localXn(localA.rows_, 1);
	Matrix localRn(localA.rows_, 1);
	Matrix localZn(localA.rows_, 1);
	Matrix localBetaZn(localA.rows_, 1);
	Matrix localAx(localA.rows_, 1);
	Matrix localAZn(localA.rows_, 1);
	Matrix localAlphaZn(localA.rows_, 1);
	Matrix localAlphaAZn(localA.rows_, 1);

	MPI_Scatterv(A.matrix_, sizes, displs, MPI_DOUBLE, localA.matrix_, sizes[rank], MPI_DOUBLE, 0, MPI_COMM_WORLD);
	for (int i = 0; i < numProcesses; ++i) {
		sizes[i] /= N;
		displs[i] /= N;
	}
	int localStartIdx = displs[rank];
	int localEndIdx = displs[rank] + sizes[rank];

	size_t counter = 0;
	DATA_TYPE alphaN = 0;
	DATA_TYPE betaN = 0;
	DATA_TYPE rnScalar = 0;
	DATA_TYPE ratio = 0;
	//get r0
	multMatrixOnMatrix(localAx, localA, xN);
	addOrSubMatrices(localRn, B, localAx, false, localStartIdx);
	MPI_Allgatherv(localRn.matrix_, sizes[rank], MPI_DOUBLE, rN.matrix_, sizes, displs, MPI_DOUBLE, MPI_COMM_WORLD);
	//get z0
	std::memcpy(zN.matrix_, rN.matrix_, sizeof(DATA_TYPE) * zN.rows_ * zN.cols_);
	DATA_TYPE normB = std::sqrt(scalarMult(B, B, localStartIdx, localEndIdx));
	while (true) {
		if (++counter >= counterLimit) {
			std::cerr << "FAIL: counter limit is reached" << std::endl;
			return -1;
		}
		rnScalar = scalarMult(rN, rN, localStartIdx, localEndIdx);
		ratio = std::sqrt(rnScalar) / normB;
		if (ratio < epsilon) {
			break;
		}

		//update alpha
		multMatrixOnMatrix(localAZn, localA, zN);
		MPI_Allgatherv(localAZn.matrix_, sizes[rank], MPI_DOUBLE, AZn.matrix_, sizes, displs, MPI_DOUBLE, MPI_COMM_WORLD);
		alphaN = rnScalar / scalarMult(AZn, zN, localStartIdx, localEndIdx);
		//update x
		multMatrixOnScalar(localAlphaZn, zN, alphaN, localStartIdx);
		addOrSubMatrices(localXn, xN, localAlphaZn, true, localStartIdx);
		MPI_Allgatherv(localXn.matrix_, sizes[rank], MPI_DOUBLE, xN.matrix_, sizes, displs, MPI_DOUBLE, MPI_COMM_WORLD);
		//update r
		multMatrixOnScalar(localAlphaAZn, AZn, alphaN, localStartIdx);
		addOrSubMatrices(localRn, rN, localAlphaAZn, false, localStartIdx);
		MPI_Allgatherv(localRn.matrix_, sizes[rank], MPI_DOUBLE, rN.matrix_, sizes, displs, MPI_DOUBLE, MPI_COMM_WORLD);
		//update beta
		betaN = scalarMult(rN, rN, localStartIdx, localEndIdx) / rnScalar;
		//update z
		multMatrixOnScalar(localBetaZn, zN, betaN, localStartIdx);
		addOrSubMatrices(localZn, rN, localBetaZn, true, localStartIdx);
		MPI_Allgatherv(localZn.matrix_, sizes[rank], MPI_DOUBLE, zN.matrix_, sizes, displs, MPI_DOUBLE, MPI_COMM_WORLD);
	}

	if (0 == rank) {
		double end = MPI_Wtime();
		std::cout << end - start << std::endl;
//		if (!writeResultToFile(xN, std::string("result.bin"))) {
//			std::cerr << "Failed to make result file" << std::endl;
//			return -2;
//		}
	}

	delete[] sizes;
	delete[] displs;
	MPI_Finalize();
	return 0;
}
