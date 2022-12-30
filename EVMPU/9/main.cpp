#include <iostream>
#include <chrono>
#include <vector>
#include <random>

using namespace std;
using duration = chrono::duration<double, nano>;
//оффсет?? ограничения на него
// как меняя оффсет можно смотреть степень ассоц разных кэшей
constexpr int OFFSET = 8 * 1024 * 1024 / sizeof(int); //8mib
constexpr int SIZE = 8 * 1024 * 1024;
constexpr int Nmin = 1;
constexpr int Nmax = 32;

duration walk(const vector<int> &vec)
{
	const volatile int *data = vec.data(); //чтобы не оптимизировал
	for (int i = 0; i < vec.size(); ++i)
	{
		data[i];
	}
	auto start = chrono::steady_clock::now();
	int i = 0;
	for (int k = data[0]; k != 0; ++i)
	{
		k = data[k];
	}
	auto end = chrono::steady_clock::now();

	return chrono::duration_cast<duration>(end - start) / i;
}

vector<int> generate(int N)
{
	vector<int> vec(N * OFFSET);
	int blockSize = SIZE / N / static_cast<int>(sizeof(int));

	for (int i = 0; i < vec.size() - OFFSET; i += OFFSET)
	{
		for (int j = 0; j < blockSize; ++j)
		{
			vec[j + i] = j + OFFSET + i;
		}
	}
	int i = ((N - 1) * OFFSET);
	for (int j = 0; j < blockSize - 1; ++j)
	{
		vec[j + i] = j + 1;
	}

	return vec;
}

int main()
{
	cout << "N;sec" << endl;
	vector<duration> durs;
	for (int N = Nmin; N <= Nmax; ++N)
	{
		duration dur(INFINITY);
		vector<int> vec = generate(N);
		for (int i = 0; i < 4; ++i)
		{
			dur = min(dur, walk(vec));
		}
		durs.push_back(dur);
		cout << N << ";" << durs[N - 1].count() << endl;
	}

	return 0;
}
