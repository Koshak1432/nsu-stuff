#include <iostream>
#include <cassert>
#include <random>
#include <algorithm>
#include <chrono>

long long partition(double *arr, long long start, long long end) {
	double pivot = arr[(start + end) / 2];
	long long i = start - 1;
	long long j = end + 1;

	while (true) {
		do {
			++i;
		} while (arr[i] < pivot);
		do {
			--j;
		} while (arr[j] > pivot);
		if (i >= j) {
			return j;
		}
		std::swap(arr[i], arr[j]);
	}
}

void qSort(double *arr, long long start, long long end, long long border) {
	assert(start >= 0);
	assert(end >= 0);
	if (start < end) {
		long long mid = partition(arr, start, end);
		#pragma omp task if (mid - start > border)
		qSort(arr, start, mid, border);

		#pragma omp task if (end - mid + 1 > border)
		qSort(arr, mid + 1, end, border);

	}
}

void generateArray(double *arr, long long size) {
	std::default_random_engine eng(412);

	std::iota(arr, arr + size, 0);
	std::shuffle(arr, arr + size, eng);
}

int main() {
	char *BARRIER = getenv("BARRIER");
	long long border = 4;
	long long size = 400000000;
	char *pEnd;
	if (nullptr != BARRIER) {
		border = strtoll(BARRIER, &pEnd, 10);
	}
	auto *array = new double[size];

	generateArray(array, size);
	auto start = std::chrono::steady_clock::now();
	#pragma omp parallel
	{
		#pragma omp single
		qSort(array, 0, size - 1, border);

	}
	auto end = std::chrono::steady_clock::now();
	std::cout << std::chrono::duration_cast<std::chrono::duration<double>>(end - start).count() << std::endl;

	return 0;
}
