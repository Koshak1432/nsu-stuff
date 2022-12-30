#ifndef LAB2_OMP__OMPSEPARATE_H
#define LAB2_OMP__OMPSEPARATE_H

#include "matrix.h"

DATA_TYPE scalarMultSeparate(Matrix &vec1, Matrix &vec2);
void addOrSubMatricesSeparate(Matrix &matrixResult, Matrix &matrixFrom, Matrix &matrixTo, bool add);
void multMatrixOnScalarSeparate(Matrix &matrixResult, Matrix &matrixSrc, DATA_TYPE scalar);
void multMatrixOnMatrixSeparate(Matrix &matrixResult, Matrix &leftMatrix, Matrix &rightMatrix);

#endif //LAB2_OMP__OMPSEPARATE_H
