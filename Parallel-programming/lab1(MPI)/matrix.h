#ifndef MPI_1_MATRIX_H
#define MPI_1_MATRIX_H

#include <cstddef>
#include <cstring>

using DATA_TYPE = double;
using std::size_t;

struct Matrix {
	Matrix(size_t rows, size_t cols) : rows_(rows), cols_(cols), matrix_(new DATA_TYPE[rows * cols]) {
		memset(matrix_, '\0', rows_ * cols_ * sizeof(DATA_TYPE));
	};
	~Matrix() {
		delete[] matrix_;
	};

	size_t rows_ = 0;
	size_t cols_ = 0;
	DATA_TYPE *matrix_ = nullptr;
};

#endif //MPI_1_MATRIX_H
