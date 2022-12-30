#ifndef LAB2_OMP__USUAL_H
#define LAB2_OMP__USUAL_H

#include "matrix.h"

DATA_TYPE scalarMult(Matrix &vec1, Matrix &vec2);
void addOrSubMatrices(Matrix &matrixResult, Matrix &matrixFrom, Matrix &matrixTo, bool add);
void multMatrixOnScalar(Matrix &matrixResult, Matrix &matrixSrc, DATA_TYPE scalar);
void multMatrixOnMatrix(Matrix &matrixResult, Matrix &leftMatrix, Matrix &rightMatrix);
#endif //LAB2_OMP__USUAL_H
