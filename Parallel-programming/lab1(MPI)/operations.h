#ifndef MPI_1_OPERATIONS_H
#define MPI_1_OPERATIONS_H

#include "matrix.h"


DATA_TYPE scalarMult(Matrix &vec1, Matrix &vec2, int startIdx, int endIdx);
void addOrSubMatrices(Matrix &matrixResult, Matrix &matrixFrom, Matrix &matrixTo, bool add, int startIdx);
void multMatrixOnScalar(Matrix &matrixResult, Matrix &matrixSrc, DATA_TYPE scalar, int startIdx);
void multMatrixOnMatrix(Matrix &matrixResult, Matrix &leftMatrix, Matrix &rightMatrix);

#endif //MPI_1_OPERATIONS_H
