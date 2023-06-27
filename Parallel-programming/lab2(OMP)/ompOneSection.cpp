#include "ompOneSection.h"

#include <cassert>
#include <algorithm>

void addOrSubMatricesOneSection(Matrix &matrixResult, Matrix &matrixFrom, Matrix &matrixTo, bool add, size_t chunks) {
	assert(matrixFrom.cols_ == matrixTo.cols_);
	assert(matrixTo.cols_ == matrixResult.cols_);
	assert(matrixFrom.rows_ == matrixTo.rows_);
	assert(matrixTo.rows_ == matrixResult.rows_);

	int sign = (add) ? 1 : -1;
	#pragma omp for SCHEDULE(chunks)
	for (size_t i = 0; i < matrixFrom.cols_ * matrixFrom.rows_; ++i) {
		matrixResult.matrix_[i] = matrixFrom.matrix_[i] + sign * matrixTo.matrix_[i];
	}
}

void multMatrixOnScalarOneSection(Matrix &matrixResult, Matrix &matrixSrc, DATA_TYPE scalar, size_t chunks) {
	assert(matrixResult.rows_ == matrixSrc.rows_);
	assert(matrixResult.cols_ == matrixSrc.cols_);
	#pragma omp for SCHEDULE(chunks)
	for (size_t i = 0; i < matrixSrc.rows_ * matrixSrc.cols_; ++i) {
		matrixResult.matrix_[i] = matrixSrc.matrix_[i] * scalar;
	}
}

//nxk * kxm
void multMatrixOnMatrixOneSection(Matrix &matrixResult, Matrix &leftMatrix, Matrix &rightMatrix, size_t chunks) {
	assert(leftMatrix.cols_ == rightMatrix.rows_);
	#pragma omp single
	{
		std::fill(matrixResult.matrix_, matrixResult.matrix_ + (matrixResult.rows_ * rightMatrix.cols_), 0);
	}

	size_t N = leftMatrix.rows_;
	size_t K = rightMatrix.rows_;
	size_t M = rightMatrix.cols_;

	#pragma omp for SCHEDULE(chunks)
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
