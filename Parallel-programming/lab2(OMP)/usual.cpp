#include "usual.h"

#include <cassert>
#include <algorithm>

DATA_TYPE scalarMult(Matrix &vec1, Matrix &vec2) {
	assert(vec1.rows_ == vec2.rows_);
	assert(vec1.cols_ == 1);
	assert(vec2.cols_ == 1);
	DATA_TYPE res = 0;
	for (size_t i = 0; i < vec1.rows_; ++i) {
		res += vec1.matrix_[i] * vec2.matrix_[i];
	}
	return res;
}

void addOrSubMatrices(Matrix &matrixResult, Matrix &matrixFrom, Matrix &matrixTo, bool add) {
	assert(matrixFrom.cols_ == matrixTo.cols_);
	assert(matrixTo.cols_ == matrixResult.cols_);
	assert(matrixFrom.rows_ == matrixTo.rows_);
	assert(matrixTo.rows_ == matrixResult.rows_);

	int sign = (add) ? 1 : -1;
	for (size_t i = 0; i < matrixFrom.cols_ * matrixFrom.rows_; ++i) {
		matrixResult.matrix_[i] = matrixFrom.matrix_[i] + sign * matrixTo.matrix_[i];
	}
}

void multMatrixOnScalar(Matrix &matrixResult, Matrix &matrixSrc, DATA_TYPE scalar) {
	assert(matrixResult.rows_ == matrixSrc.rows_);
	assert(matrixResult.cols_ == matrixSrc.cols_);

	for (size_t i = 0; i < matrixSrc.rows_ * matrixSrc.cols_; ++i) {
		matrixResult.matrix_[i] = matrixSrc.matrix_[i] * scalar;
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

	/*
	for (size_t i = 0; i < N; ++i) {
		for (size_t k = 0; k < K; ++k) {
			for (size_t j = 0; j < M; ++j) {
				matrixResult.matrix_[M * i + j] += leftMatrix.matrix_[K * i + k] * rightMatrix.matrix_[M * k + j];
			}
		}
	}
	 */
}
