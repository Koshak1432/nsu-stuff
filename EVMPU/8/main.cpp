#include <iostream>
#include <chrono>
#include <vector>
#include <random>
#include <algorithm>

using namespace std;
using duration = chrono::duration<double, nano>;

constexpr int Nmin = 1024 / sizeof(int);
constexpr int Nmax = 256 * 1024 * 1024 / sizeof(int);
constexpr int K = 20;

duration walk(const vector<int> &vec)
{
	const volatile int *data = vec.data();
	for (int i = 0; i < vec.size(); ++i)
	{
		data[i];
	}
	auto start = chrono::steady_clock::now();
	for (int k = 0, i = 0; i < vec.size() * K; ++i)
	{
		k = data[k];
	}
	auto end = chrono::steady_clock::now();

	return chrono::duration_cast<duration>(end - start) / (vec.size() * K); //среднее время
}

vector<int> generateForward(int size)
{
	vector<int> vec(size);
	for (int i = 0; i < size - 1; ++i)
	{
		vec[i] = i + 1;
	}
	vec[size - 1] = 0;
	return vec;
}

vector<int> generateBackward(int size)
{
	vector<int> vec(size);
	for (int i = size - 1; i > 0; --i)
	{
		vec[i] = i - 1;
	}
	vec[0] = size - 1;
	return vec;
}

vector<int> generateRandom(int size)
{
	vector<int> dummyVec(size);
	random_device generator;
	default_random_engine eng(generator());

	for (int i = 0; i < dummyVec.size(); ++i)
	{
		dummyVec[i] = i;
	}
	shuffle(dummyVec.begin(), dummyVec.end(), eng);
// --> 128 mib
// l3 steps
	vector<int> vec(size);
	for (int i = 0; i < vec.size() - 1; ++i)
	{
		vec[dummyVec[i]] = dummyVec[i + 1];
	}
	vec[dummyVec[size - 1]] = dummyVec[0];
	return vec;
}

int main()
{
	cout << "Size(KiB);Forward;Backward;Random" << endl;
	for (int i = Nmin; i <= Nmax; )
	{
		int runs = i < 2 * 1024 * 1024 / sizeof(int) ? 8 : 1;
		duration forwardDur(INFINITY);
		duration backwardDur(INFINITY);
		vector<duration> durs;

		for (int j = 0; j < runs; ++j)
		{
			vector<int> vecForward = generateForward(i);
			vector<int> vecBackward = generateBackward(i);
			vector<int> vecRandom = generateRandom(i);

			forwardDur = min(forwardDur, walk(vecForward));
			backwardDur = min(backwardDur, walk(vecBackward));
			durs.push_back(walk(vecRandom));
		}
		sort(durs.begin(), durs.end());
		duration randomDur = durs[runs / 2];

		cout << i * sizeof(int) / 1024 << ";" << forwardDur.count() << ";" << backwardDur.count() << ";" << randomDur.count() << endl;

		if (i < 256 * 32) // [0; 32kib]
		{
			i += 256; //1kib
		}
		else if (i < 256 * 768) //[32; 768]
		{
			i += 256 * 16; // += 16kib
		}
		else if (i < 256 * 1024 * 6) //[768; 6m]
		{
			i += 256 * 64;
		}
		else
		{
			i += 256 * 1024;
		}
	}
	return 0;
}
