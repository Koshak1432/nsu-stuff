#include <iostream>
#include <vector>
#include <fstream>
#include <cmath>
#include <algorithm>
#include <random>
#include <omp.h>

#include "usual.h"
#include "ompSeparate.h"
#include "ompOneSection.h"

#define OMP_SEPARATE_TYPE "-separate"
#define OMP_ONE_TYPE "-one_section"

constexpr double epsilon = 1e-6;
constexpr size_t counterLimit = 10000;
constexpr size_t N = 50;

enum Type {
	DEFAULT = 0,
	SEPARATE,
	ONE_SECTION
};

void formAMatrix(Matrix &matrix) {
	std::default_random_engine eng(412);
	std::uniform_real_distribution<DATA_TYPE> dis(1, 47);

	for (size_t row = 0; row < matrix.rows_; ++row) {
		for (size_t col = 0; col <= row; ++col) {
			matrix.matrix_[row * matrix.rows_ + col] = dis(eng);
			matrix.matrix_[col * matrix.cols_ + row] = matrix.matrix_[row * matrix.rows_ + col];
			if (row * matrix.rows_ + col == col * matrix.cols_ + row) {  // diag * 5
				matrix.matrix_[row * matrix.rows_ + col] *= 5;
			}
		}
	}
}

void formXMatrix(Matrix &matrix) {
	std::default_random_engine eng(311);
	std::uniform_real_distribution<DATA_TYPE> dis(1, 47);

	for (size_t i = 0; i < matrix.rows_; ++i) {
		matrix.matrix_[i] = dis(eng);
	}
}

bool writeResultToFile(Matrix &matrix, const std::string &name) {
	std::ofstream out(name, std::ios::out | std::ios::binary | std::ios::trunc);
	if (out.fail()) {
//		std::cerr << "Couldn't open output file" << std::endl;
		return false;
	}
	try {
		out.write(reinterpret_cast<const char *>(matrix.matrix_), matrix.cols_ * matrix.rows_ * sizeof(DATA_TYPE));
	} catch (std::exception &e) {
		out.close();
//		std::cerr << "Caught an exception: " << e.what() << std::endl;
		return false;
	}
	out.close();
	return true;
}

void writeAllInputFiles() {
	Matrix A(N * N, N * N);
	formAMatrix(A);
	if (!writeResultToFile(A, std::string("A.bin"))) {
		std::cerr << "Failed to make result file" << std::endl;
		return;
	}
	Matrix X(N * N, 1);
	formXMatrix(X);
	if (!writeResultToFile(X, std::string("X.bin"))) {
		std::cerr << "Failed to make result file" << std::endl;
		return;
	}
	Matrix B(N * N, 1);
	multMatrixOnMatrix(B, A, X);
	if (!writeResultToFile(B, std::string("B.bin"))) {
		std::cerr << "Failed to make result file" << std::endl;
		return;
	}
}

bool doUsual(Matrix &A, Matrix &B, Matrix &xN, Matrix &rN, Matrix &zN, Matrix &AZn, Matrix &alphaZn, Matrix &Ax) {
	size_t counter = 0;
	DATA_TYPE alphaN = 0;
	DATA_TYPE betaN = 0;
	DATA_TYPE rnScalar = 0;
	DATA_TYPE ratio = 0;
	//get r0
	multMatrixOnMatrix(Ax, A, xN);
	addOrSubMatrices(rN, B, Ax, false);
	//get z0
	std::memcpy(zN.matrix_, rN.matrix_, sizeof(DATA_TYPE) * zN.rows_ * zN.cols_);
	DATA_TYPE normB = std::sqrt(scalarMult(B, B));

	while (true) {
		if (++counter >= counterLimit) {
			std::cerr << "FAIL: counter limit is reached" << std::endl;
			return false;
		}
		rnScalar = scalarMult(rN, rN);
		ratio = std::sqrt(rnScalar) / normB;
		if (ratio < epsilon) {
			break;
		}
		//update alpha
		multMatrixOnMatrix(AZn, A, zN);
		alphaN = rnScalar / scalarMult(AZn, zN);
		//update x
		multMatrixOnScalar(alphaZn, zN, alphaN);
		addOrSubMatrices(xN, xN, alphaZn, true);
		//update r
		multMatrixOnMatrix(AZn, A, zN);
		multMatrixOnScalar(AZn, AZn, alphaN);
		addOrSubMatrices(rN, rN, AZn, false);
		//update beta
		betaN = scalarMult(rN, rN) / rnScalar;
		//update z
		multMatrixOnScalar(zN, zN, betaN);
		addOrSubMatrices(zN, zN, rN, true);
	}
	return true;
}

bool doSeparate(Matrix &A, Matrix &B, Matrix &xN, Matrix &rN, Matrix &zN, Matrix &AZn, Matrix &alphaZn, Matrix &Ax) {
	size_t counter = 0;
	DATA_TYPE alphaN = 0;
	DATA_TYPE betaN = 0;
	DATA_TYPE rnScalar = 0;
	DATA_TYPE ratio = 0;
	//get r0
	multMatrixOnMatrixSeparate(Ax, A, xN);
	addOrSubMatricesSeparate(rN, B, Ax, false);

	std::memcpy(zN.matrix_, rN.matrix_, sizeof(DATA_TYPE) * zN.rows_ * zN.cols_);
	DATA_TYPE normB = std::sqrt(scalarMultSeparate(B, B));

	while (true) {
		if (++counter >= counterLimit) {
			std::cerr << "FAIL: counter limit is reached" << std::endl;
			return false;
		}
		rnScalar = scalarMultSeparate(rN, rN);
		ratio = std::sqrt(rnScalar) / normB;
		if (ratio < epsilon) {
			break;
		}
		//update alpha
		multMatrixOnMatrixSeparate(AZn, A, zN);
		alphaN = rnScalar / scalarMultSeparate(AZn, zN);
		//update x
		multMatrixOnScalarSeparate(alphaZn, zN, alphaN);
		addOrSubMatricesSeparate(xN, xN, alphaZn, true);
		//update r
		multMatrixOnMatrixSeparate(AZn, A, zN);
		multMatrixOnScalarSeparate(AZn, AZn, alphaN);
		addOrSubMatricesSeparate(rN, rN, AZn, false);
		//update beta
		betaN = scalarMultSeparate(rN, rN) / rnScalar;
		//update z
		multMatrixOnScalarSeparate(zN, zN, betaN);
		addOrSubMatricesSeparate(zN, zN, rN, true);
	}
	return true;
}

bool doOneSection(Matrix &A, Matrix &B, Matrix &xN, Matrix &rN, Matrix &zN, Matrix &AZn, Matrix &alphaZn, Matrix &Ax, size_t chunks) {
	DATA_TYPE normB = 0;
	DATA_TYPE ratio = 0;
	DATA_TYPE rnScalar = 0;
	DATA_TYPE tmpScalar = 0;
	bool fail = false;

	#pragma omp parallel
	{
		DATA_TYPE alphaN = 0;
		DATA_TYPE betaN = 0;
		size_t counter = 0;
		//get r0
		multMatrixOnMatrixOneSection(Ax, A, xN, chunks);
		addOrSubMatricesOneSection(rN, B, Ax, false, chunks);
		//get z0
		#pragma omp single nowait
		{
			std::memcpy(zN.matrix_, rN.matrix_, sizeof(DATA_TYPE) * zN.rows_ * zN.cols_);
		}
		//scalarMultOneSection(B, B, normB);
		#pragma omp for SCHEDULE(chunks) reduction(+: normB)
		for (size_t i = 0; i < B.rows_; ++i) {
			normB += B.matrix_[i] * B.matrix_[i];
		}
		#pragma omp single
		{
			normB = std::sqrt(normB);
		}

		while (true) {
			if (++counter >= counterLimit) {
				std::cerr << "FAIL: counter limit is reached" << std::endl;
				fail = true;
				break;
			}
//			scalarMultOneSection(rN, rN, rnScalar);
			#pragma omp for SCHEDULE(chunks) reduction(+: rnScalar)
			for (size_t i = 0; i < rN.rows_; ++i) {
				rnScalar += rN.matrix_[i] * rN.matrix_[i];
			}
			#pragma omp single
			{
				ratio = std::sqrt(rnScalar) / normB;
			}
			if (ratio < epsilon) {
				break;
			}

			//update alpha
			multMatrixOnMatrixOneSection(AZn, A, zN, chunks);
			//scalarMultOneSection(AZn, zN, tmpScalar);
			#pragma omp for SCHEDULE(chunks) reduction(+: tmpScalar)
			for (size_t i = 0; i < AZn.rows_; ++i) {
				tmpScalar += AZn.matrix_[i] * zN.matrix_[i];
			}
			alphaN = rnScalar / tmpScalar;
			//update x
			multMatrixOnScalarOneSection(alphaZn, zN, alphaN, chunks);
			addOrSubMatricesOneSection(xN, xN, alphaZn, true, chunks);
			//update r
			multMatrixOnMatrixOneSection(AZn, A, zN, chunks);
			multMatrixOnScalarOneSection(AZn, AZn, alphaN, chunks);
			addOrSubMatricesOneSection(rN, rN, AZn, false, chunks);
			//update beta
			#pragma omp single
			{
				tmpScalar = 0;
			}
			//scalarMultOneSection(rN, rN, tmpScalar);
			#pragma omp for SCHEDULE(chunks) reduction(+: tmpScalar)
			for (size_t i = 0; i < rN.rows_; ++i) {
				tmpScalar += rN.matrix_[i] * rN.matrix_[i];
			}
			betaN = tmpScalar / rnScalar;
			//update z
			multMatrixOnScalarOneSection(zN, zN, betaN, chunks);
			addOrSubMatricesOneSection(zN, zN, rN, true, chunks);
			#pragma omp single
			{
				rnScalar = 0;
				tmpScalar = 0;
			};
		}
	}
	return !fail;
}

int main(int argc, char **argv) {
//	writeAllInputFiles();
	if (argc < 4) {
		std::cerr << "Gimme the files" << std::endl;
		return -1;
	}
	Type type = DEFAULT;
	if (strcmp(argv[3], OMP_SEPARATE_TYPE) == 0) {
		type = SEPARATE;
	} else if (strcmp(argv[3], OMP_ONE_TYPE) == 0) {
		type = ONE_SECTION;
	}

	char *CHUNKS = getenv("CHUNKS");
	size_t chunks = 1;
	char *pEnd;
	if (nullptr != CHUNKS) {
		chunks = strtoul(CHUNKS, &pEnd, 10);
	}

	std::ifstream file(argv[1]);
	if (file.fail()) {
		std::cerr << "Failed to open " << argv[1] <<std::endl;
		return -2;
	}

	Matrix A(N * N, N * N);
	try {
		file.read(reinterpret_cast<char *>(A.matrix_), N * N * N * N * sizeof(DATA_TYPE));
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

	Matrix B(N * N, 1);
	try {
		file.read(reinterpret_cast<char *>(B.matrix_), N * N * sizeof(DATA_TYPE));
	} catch (std::exception &e) {
		file.close();
		std::cerr << "Caught an exception: " << e.what() << std::endl;
		return -5;
	}
	file.close();

	Matrix xN(N * N, 1);
	Matrix rN(A.rows_, xN.cols_);
	Matrix zN(rN.rows_, rN.cols_);
	//buffers
	Matrix AZn(A.rows_, zN.cols_);
	Matrix Ax(A.rows_, xN.cols_);
	Matrix alphaZn(zN.rows_, zN.cols_);

	double start = omp_get_wtime();
	switch (type) {
		case SEPARATE:
			if (!doSeparate(A, B, xN, rN, zN, AZn, alphaZn, Ax)) {
				std::cerr << "Couldn't calculate separate" << std::endl;
				return -3;
			}
			break;
		case ONE_SECTION:
			if (!doOneSection(A, B, xN, rN, zN, AZn, alphaZn, Ax, chunks)) {
				std::cerr << "Couldn't calculate oneSection" << std::endl;
				return -3;
			}
			break;
		default:
			if (!doUsual(A, B, xN, rN, zN, AZn, alphaZn, Ax)) {
				std::cerr << "Couldn't calculate usual" << std::endl;
				return -3;
			}
			break;
	}
	double end = omp_get_wtime();
	std::cout << end - start << std::endl;

	if (!writeResultToFile(xN, std::string("result.bin"))) {
		std::cerr << "Failed to make result file" << std::endl;
		return -2;
	}
	return 0;
}
