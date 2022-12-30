#include "matrix.h"

int main()
{
	auto *matrix = new(std::align_val_t{32}) float[N * N];
	auto *inverseMatrix = new(std::align_val_t{32}) float[N * N];
	std::fill(inverseMatrix, inverseMatrix + N * N, 0);

	for (int k = 0; k < N; ++k)
	{
		for (int i = 0; i < N; ++i)
		{
			matrix[k * N + i] = rand() % 10;
		}
	}

	double res = matrixInverse(matrix, inverseMatrix, INTRINSIC_OPT);
	std::cout << res << " ms" << std::endl;

	delete[](matrix);
	delete[](inverseMatrix);
	return 0;
}