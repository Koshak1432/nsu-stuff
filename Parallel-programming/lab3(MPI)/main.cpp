//#include <mpi.h>
#include <C:\Program Files (x86)\Microsoft MPI\MPI_SDK\Include\mpi.h
#include <iostream>
#include <iomanip>
#include <random>

#include "Matrix.h"

constexpr int N1 = 3840;
constexpr int N2 = 3840;
constexpr int N3 = 3840;
constexpr int NDIMS = 2;

void fillDispls(int *displs, const Matrix &localC, int *dims, MPI_Comm grid) {
	int coords[2] = {0, 0};
	int rank = 0;
	for (int i = 0; i < dims[0]; ++i) {
		for (int j = 0; j < dims[1]; ++j) {
			coords[0] = i;
			coords[1] = j;
			MPI_Cart_rank(grid, coords, &rank);
			displs[rank] = dims[1] * i * localC.rows_ + j;
		}
	}
}

void createComms(MPI_Comm *grid, MPI_Comm *subgridRows, MPI_Comm *subgridCols, int *dims) {
	int periods[2] = {0, 0};
	int reorder = 0;
	MPI_Cart_create(MPI_COMM_WORLD, NDIMS, dims, periods, reorder, grid);
	int remainDims[2] = {0, 1};
	MPI_Cart_sub(*grid, remainDims, subgridRows);
	remainDims[0] = 1;
	remainDims[1] = 0;
	MPI_Cart_sub(*grid, remainDims, subgridCols);
}

void getCoords(MPI_Comm grid, int *dims, int *coords) {
	int periods[2] = {0, 0};
	MPI_Cart_get(grid, NDIMS, dims, periods, coords);
}

void formMatrix(Matrix &matrix) {
	std::default_random_engine eng(412);
	std::uniform_real_distribution<DATA_TYPE> dis(1, 97);

	for (size_t i = 0; i < matrix.rows_ * matrix.cols_; ++i) {
		matrix.matrix_[i] = dis(eng);
	}
}

//nxk * kxm
void multiply(const Matrix &localA, const Matrix &localB, Matrix &localC) {
	size_t N = localA.rows_;
	size_t K = localB.rows_;
	size_t M = localB.cols_;

	for (size_t row = 0; row < N; ++row) {
		DATA_TYPE *c = localC.matrix_ + row * M;
		for (size_t k = 0; k < K; ++k) {
			const DATA_TYPE *b = localB.matrix_ + k * M;
			const DATA_TYPE a = localA.matrix_[row * K + k];

			for (size_t j = 0; j < M; ++j) {
				c[j] += a * b[j];
			}
		}
	}
}

void printMatrix(const Matrix &matrix) {
	for (int i = 0; i < matrix.rows_; ++i) {
		for (int j = 0; j < matrix.cols_; ++j) {
			std::cout << std::setprecision(2) << matrix.matrix_[i * matrix.cols_ + j] << "\t";
		}
		std::cout << std::endl;
	}
}

void scatterA(const Matrix &A, Matrix &localA, MPI_Comm subgridCols) {
	int localSize = localA.rows_ * localA.cols_;
	MPI_Scatter(A.matrix_, localSize, MPI_DOUBLE, localA.matrix_, localSize, MPI_DOUBLE, 0, subgridCols);
}

//type_vector: число блоков, длина блока, дырка
void scatterB(const Matrix &B, Matrix &localB, MPI_Comm subgridRows) {
	MPI_Datatype colTypeNotResized, colType;

	MPI_Type_vector(localB.rows_, localB.cols_, B.cols_, MPI_DOUBLE, &colTypeNotResized);
	MPI_Type_create_resized(colTypeNotResized, 0, localB.cols_ * sizeof(double), &colType);
	MPI_Type_commit(&colType);
	MPI_Scatter(B.matrix_, 1, colType, localB.matrix_, localB.rows_ * localB.cols_, MPI_DOUBLE, 0, subgridRows);
}

//type_vector: число блоков, длина блока, дырка
void collectResults(Matrix &C, Matrix &localC, MPI_Comm grid, int *displs, int *recvcounts) {
	MPI_Datatype cBlockNotResized, cBlock;

	MPI_Type_vector(localC.rows_, localC.cols_, C.cols_, MPI_DOUBLE, &cBlockNotResized);
	MPI_Type_create_resized(cBlockNotResized, 0, localC.cols_ * sizeof(double), &cBlock);
	MPI_Type_commit(&cBlock);
	MPI_Gatherv(localC.matrix_, localC.rows_ * localC.cols_, MPI_DOUBLE, C.matrix_, recvcounts, displs, cBlock, 0, grid);
}

int main(int argc, char **argv) {
	MPI_Init(&argc, &argv);
	double start = 0;
	int dims[2] = {0, 0};
	int coords[2] = {0, 0};
	int worldRank = 0;
	int gridRank = 0;
	int numProcesses = 0;

	MPI_Comm grid;
	MPI_Comm subgridRows;
	MPI_Comm subgridCols;
	MPI_Comm_rank(MPI_COMM_WORLD, &worldRank);
	MPI_Comm_size(MPI_COMM_WORLD, &numProcesses);
	if (0 == worldRank) {
		start = MPI_Wtime();
	}

	MPI_Dims_create(numProcesses, NDIMS, dims);
	int numHorStripes = dims[0]; //sizeX
	int numVertStripes = dims[1]; //sizeY
	if (N1 % numHorStripes != 0 || N3 % numVertStripes != 0) {
		std::cerr << "Incorrect: N1 == " << N1 << ", N3 == " << N3 << ", sizeX: " << numHorStripes << ", sizeY: " << numVertStripes << std::endl;
		return -1;
	}
	int displs[numProcesses];
	int recvcounts[numProcesses];
	memset(recvcounts, 1, numProcesses * sizeof(int));
	memset(displs, 0, numProcesses * sizeof(int));

	createComms(&grid, &subgridRows, &subgridCols, dims);
	getCoords(grid, dims, coords);
	MPI_Comm_rank(grid, &gridRank);

	Matrix A(N1, N2);
	Matrix B(N2, N3);
	Matrix C(N1, N3);

	Matrix localA(N1 / numHorStripes, N2);
	Matrix localB(N2, N3 / numVertStripes);
	Matrix localC(N1 / numHorStripes, N3 / numVertStripes);

	if (0 == worldRank) {
		formMatrix(A);
		formMatrix(B);
	}

	fillDispls(displs, localC, dims, grid);
	if (0 == coords[1]) {
		scatterA(A, localA, subgridCols);
	}
	if (0 == coords[0]) {
		scatterB(B, localB, subgridRows);
	}

	MPI_Bcast(localA.matrix_, localA.cols_ * localA.rows_, MPI_DOUBLE, 0, subgridRows);
	MPI_Bcast(localB.matrix_, localB.cols_ * localB.rows_, MPI_DOUBLE, 0, subgridCols);

	multiply(localA, localB, localC);
	collectResults(C, localC, grid, displs, recvcounts);

	if (0 == worldRank) {
		double end = MPI_Wtime();
		std::cout << end - start << std::endl;
	}
	MPI_Finalize();
	return 0;
}
