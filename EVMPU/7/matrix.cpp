#include "matrix.h"

#include "eigen-3.4.0/Eigen/Core"

#include <ctime>
#include <cassert>
#include <immintrin.h>

void mul(const float *A, const float *B, float *C, Optimization type)
{
	switch (type)
	{
		case WITHOUT_OPT:
		{
			mulDefault(A, B, C);
			break;
		}
		case INTRINSIC_OPT:
		{
			mulIntrinsic(A, B, C);
			break;
		}
		case EIGEN_OPT:
		{
			mulEigen(A, B, C);
			break;
		}
		default:
		{
			assert(false);
		}
	}
}

void mulEigen(const float *A, const float *B, float *C)
{
	Eigen::Map<const Eigen::MatrixXf> matrixA(A, N, N);
	Eigen::Map<const Eigen::MatrixXf> matrixB(B, N, N);
	Eigen::Map<Eigen::MatrixXf> res(C, N, N);
	res = matrixA * matrixB;
}

void mulIntrinsic(const float *A, const float *B, float *C)
{
	std::fill(C, C + N * N, 0);
	for (int i = 0; i < N; ++i)
	{
		float * c = C + i * N;
		for (int k = 0; k < N; ++k)
		{
			const float * b = B + k * N;
			__m256 a = _mm256_set1_ps(A[i * N + k]);
			for (int j = 0; j < N; j += 8)
			{
				_mm256_storeu_ps(c + j, _mm256_fmadd_ps(a, _mm256_loadu_ps(b + j), _mm256_loadu_ps(c + j)));
			}
		}
	}
}

void mulDefault(const float *A, const float *B, float *C)
{
	std::fill(C, C + N * N, 0);
	for (int i = 0; i < N; ++i)
	{
		float *c = C + i * N;

		for (int k = 0; k < N; ++k)
		{
			const float *b = B + k * N;
			const float a = A[i * N + k];
			for (int j = 0; j < N; ++j)
			{
				c[j] += a * b[j];
			}
		}
	}
}

void sum(float *A, float *B, float *C, bool sign, Optimization type)
{
	if (type != INTRINSIC_OPT)
	{
		float sign2;
		(sign) ? sign2 = -1 : sign2 = 1;
		for (int i = 0; i < N; ++i)
		{
			float *a = A + i * N;
			float *b = B + i * N;
			float *c = C + i * N;
			for (int j = 0; j < N; ++j)
			{
				c[j] = a[j] + sign2 * b[j];
			}
		}
	}
	else
	{
		__m256 signArr, p;
		float digitSign = 1;
		if (sign)
		{
			digitSign *= -1;
		}
		signArr = _mm256_set1_ps(digitSign);

		for (int i = 0; i < N; ++i)
		{
			__m256 *xx, *yy;
			xx = (__m256 *) &A[i * N];
			yy = (__m256 *) &B[i * N];
			for (int k = 0; k < N / 8; ++k)
			{
				p = _mm256_add_ps(xx[k], yy[k]);
				p = _mm256_mul_ps(p, signArr);
				_mm256_store_ps(&C[i * N + k * 8], p);
			}
		}
	}
}

double matrixInverse(float *matrix, float *result, Optimization type)
{
	clock_t c_start = clock();

	auto *I = new(std::align_val_t{32}) float[N * N];
	auto *B = new(std::align_val_t{32}) float[N * N];
	auto *tmp = new(std::align_val_t{32}) float[N * N];
	auto *R = new(std::align_val_t{32}) float[N * N];

	{
		float max0 = 0; //rows max
		float max1 = 0; //cols max
		float sum = 0; 	//rows sum
		float sum1 = 0; //cols sum

		for (int i = 0; i < N; ++i)
		{
			I[N * i + i] = 1;
			for (int j = 0; j < N; ++j)
			{
				sum += std::abs(matrix[N * i + j]);
				sum1 += std::abs(matrix[j * N + i]);
			}

			max1 = std::max(max1, sum1);
			max0 = std::max(max0, sum);

			sum = 0;
			sum1 = 0;
		}
		float max = max1 * max0;

		std::copy(matrix, matrix + N * N, B);
		for (int i = 0; i < N; ++i)
		{
			for (int j = i + 1; j < N; ++j)
			{
				float &l = B[N * i + j];
				float &r = B[j * N + i];
				std::swap(l, r);
			}
		}

		for (int i = 0; i < N; ++i)
		{
			for (int j = 0; j < N; ++j)
			{
				B[i * N + j] /= max;
			}
		}
	}

	mul(B, matrix, tmp, type); 				//tmp = BA
	sum(I, tmp, R, true, type);		//R = I - BA
	std::copy(I, I + N * N, result);
	std::copy(R, R + N * N, I);			//I = R

	for (int k = 0; k < ITERATIONS_NUM; ++k)
	{
		sum(result, I, result, false, type); 	//res += R^(k + 1)
		mul(I, R, tmp, type); 						//tmp = R^(k + 2)
		std::swap(I, tmp);
	}

	mul(result, B, tmp, type);
	std::copy(tmp, tmp + N * N, result);

	clock_t c_end = clock();
	double timeElapsed_ = 1000.0 * ((double) (c_end - c_start) / CLOCKS_PER_SEC);

	delete[](I);
	delete[](B);
	delete[](tmp);
	delete[](R);

	return timeElapsed_;
}



