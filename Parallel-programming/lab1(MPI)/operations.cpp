#include "operations.h"

#include <C:\Program Files (x86)\Microsoft MPI\MPI_SDK\Include\mpi.h>
//#include <mpi.h>
#include <iostream>
#include <cassert>
#include <algorithm>


DATA_TYPE scalarMult(Matrix &vec1, Matrix &vec2, int startIdx, int endIdx) {
	assert(vec1.rows_ == vec2.rows_);
	assert(vec1.cols_ == 1);
	assert(vec2.cols_ == 1);
	DATA_TYPE res = 0;
	DATA_TYPE sum = 0;
	for (size_t i = startIdx; i < endIdx; ++i) {
		sum += vec1.matrix_[i] * vec2.matrix_[i];
	}
	MPI_Allreduce(&sum, &res, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
	return res;
}

void addOrSubMatrices(Matrix &matrixResult, Matrix &matrixFrom, Matrix &matrixTo, bool add, int startIdx) {
	int sign = (add) ? 1 : -1;
	for (size_t i = 0; i < matrixResult.rows_; ++i) {
		matrixResult.matrix_[i] = matrixFrom.matrix_[startIdx + i] + sign * matrixTo.matrix_[i];
	}
}

void multMatrixOnScalar(Matrix &matrixResult, Matrix &matrixSrc, DATA_TYPE scalar, int startIdx) {

	for (size_t i = 0; i < matrixResult.rows_; ++i) {
		matrixResult.matrix_[i] = matrixSrc.matrix_[startIdx + i] * scalar;
	}
}

//nxk * kxm
void multMatrixOnMatrix(Matrix &matrixResult, Matrix &leftMatrix, Matrix &rightMatrix) {
	assert(leftMatrix.cols_ == rightMatrix.rows_);
	std::fill(matrixResult.matrix_, matrixResult.matrix_ + (matrixResult.rows_ * rightMatrix.cols_), 0);

	size_t N = leftMatrix.rows_;
	size_t K = rightMatrix.rows_;
	size_t M = rightMatrix.cols_;

	for (size_t row = 0; row < N; ++row) {
		DATA_TYPE *c = matrixResult.matrix_ + row * M;
		for (size_t k = 0; k < K; ++k) {
			const DATA_TYPE *b = rightMatrix.matrix_ + k * M;
			const DATA_TYPE a = leftMatrix.matrix_[row * K + k];

			for (size_t j = 0; j < M; ++j) {
				c[j] += a * b[j];
			}
		}
	}
}

