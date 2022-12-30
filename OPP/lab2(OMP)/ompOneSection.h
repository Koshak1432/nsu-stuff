#ifndef LAB2_OMP__OMPONESECTION_H
#define LAB2_OMP__OMPONESECTION_H

#include "matrix.h"

#define SCHEDULE(chunks) schedule(static, (chunks))
//#define SCHEDULE(chunks) schedule(dynamic, (chunks))
//#define SCHEDULE(chunks) schedule(guided)

void addOrSubMatricesOneSection(Matrix &matrixResult, Matrix &matrixFrom, Matrix &matrixTo, bool add, size_t chunks);
void multMatrixOnScalarOneSection(Matrix &matrixResult, Matrix &matrixSrc, DATA_TYPE scalar, size_t chunks);
void multMatrixOnMatrixOneSection(Matrix &matrixResult, Matrix &leftMatrix, Matrix &rightMatrix, size_t chunks);
#endif //LAB2_OMP__OMPONESECTION_H
