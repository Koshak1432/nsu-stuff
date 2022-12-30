#ifndef LAB7_MATRIX_H
#define LAB7_MATRIX_H

#include <iostream>

constexpr std::size_t N = 2048;
constexpr std::size_t ITERATIONS_NUM = 10;

enum Optimization
{
	WITHOUT_OPT,
	INTRINSIC_OPT,
	EIGEN_OPT,
};

double matrixInverse(float *matrix, float *result, Optimization type);
void mul(const float *A, const float *B, float *C, Optimization type);
void mulDefault(const float *A, const float *B, float *C);
void mulEigen(const float *A, const float *B, float *C);
void mulIntrinsic(const float *A, const float *B, float *C);
void sum(float *A, float *B, float *C, bool sign, Optimization type);


#endif //LAB7_MATRIX_H
