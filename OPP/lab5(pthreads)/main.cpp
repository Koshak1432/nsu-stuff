#include <iostream>
#include <vector>
#include <shared_mutex>
#include <functional>
#include <condition_variable>
#include <thread>
#include <chrono>

using namespace std;
using mutex_t = shared_mutex;
using writeLock = unique_lock<mutex_t>;
using readLock = shared_lock<mutex_t>;

condition_variable_any canPop;

template<class T>
class Container {
public:
	explicit Container(size_t size) : data_(std::vector<T>()) {
		data_.reserve(size);
	}

	void put(T elem) {
		writeLock guard(mtx);
		data_.push_back(elem);
		canPop.notify_one();
	}

	T pop() {
		writeLock guard(mtx);
		canPop.wait(guard, [this] {return !data_.empty();});
		T elem = data_.back();
		data_.pop_back();
		return elem;
	}

	T topU() {
		writeLock guard(mtx);
		canPop.wait(guard, [this] {return !data_.empty();});
		T elem = data_.back();
		return elem;
	}

	T top() {
		readLock guard(mtx);
		canPop.wait(guard, [this] {return !data_.empty();});
		T elem = data_.back();
		return elem;
	}

	void map(const std::function<T(T)> &func) {
		writeLock guard(mtx);
		for (int i = 0; i < data_.size(); ++i) {
			data_[i] = func(data_[i]);
		}
	}

	void clear() {
		data_.clear();
	}

	void printData() {
		cout << "=========\tSTART PRINT DATA\t=========" << endl;
		for (size_t i = 0; i < data_.size(); ++i) {
			std::cout << data_[i] << std::endl;
		}
		cout << "=========\tEND PRINT DATA\t=========" << endl;
	}

private:
	std::vector<T> data_;
	mutex_t mtx;
};

int throwFunc(int elem) {
	cout << "Thread № " << this_thread::get_id() << " is going to throw an exception" << endl;
	throw std::invalid_argument("HAHA\n");
}

int noThrow(int elem) {
	cout << "Thread № " << this_thread::get_id() << " is alive" << endl;
	return elem;
}

void throwTest(Container<int> &container) {
	try {
		container.map(&throwFunc);
	} catch (exception &e) {
		cout << "Got the exception from thread № " << this_thread::get_id() << endl;
	}
}

void noThrowTest(Container<int> &container) {
	container.map(&noThrow);
}

void topTest(Container<int> &container) {
	size_t iterationsNum = 1000 * 1000;
	for (size_t i = 0; i < iterationsNum; ++i) {
		int j = container.top();
	}
}

void topUTest(Container<int> &container) {
	size_t iterationsNum = 1000 * 1000;
	for (size_t i = 0; i < iterationsNum; ++i) {
		int j = container.topU();
	}
}

void delayedPutNElems(Container<int> &container, size_t N) {
	for (size_t i = 0; i < N - 1; ++i) {
		cout << "put " << i << endl;
		container.put(i);
	}
	size_t timeToSleep = 8;
	cout << "Now I'll sleep for " << timeToSleep << " seconds" << endl;
	this_thread::sleep_for(chrono::seconds(timeToSleep));
	cout << "Enough sleeping" << endl;
	container.put(N - 1);
}

void getNElems(Container<int> &container, size_t N) {
	for (size_t i = 0; i < N; ++i) {
		cout << "Got " << container.pop() << endl;
	}
	cout << "Got all elems" << endl;
}

int main() {
	size_t N = 15;
	Container<int> container(N);
	vector<thread> threads;
	threads.reserve(N);

	/*
	cout << "=========\tSTART TEST 1\t=========" << endl;
	{
		for (size_t i = 0; i < N; ++i) {
			threads.emplace_back(&Container<int>::put, ref(container), i);
		}
		for (auto &thread : threads) {
			thread.join();
		}
		container.printData();
		threads.clear();


		for (size_t i = 0; i < N; ++i) {
			threads.emplace_back([&]() {
				cout << "popped " << container.pop() << " from the container" << endl;
			});
		}
		for (auto &thread : threads) {
			thread.join();
		}
		container.printData();
		threads.clear();
		container.clear();
	}
	cout << "=========\tEND TEST 1\t=========" << endl;

	cout << "=========\tSTART TEST 2\t=========" << endl;
	{
		for (size_t i = 0; i < N; ++i) {
			threads.emplace_back(&Container<int>::put, ref(container), i);
		}
		for (auto &thread : threads) {
			thread.join();
		}
		threads.clear();

		auto start = chrono::steady_clock::now();
		for (size_t i = 0; i < N; ++i) {
			threads.emplace_back(topTest, ref(container));
		}
		for (auto &thread : threads) {
			thread.join();
		}
		auto end = chrono::steady_clock::now();
		cout << "TOP TEST(SEC): " << chrono::duration_cast<chrono::seconds>(end - start).count() << endl;
		threads.clear();

		start = chrono::steady_clock::now();
		for (size_t i = 0; i < N; ++i) {
			threads.emplace_back(topUTest, ref(container));
		}
		for (auto &thread : threads) {
			thread.join();
		}
		end = chrono::steady_clock::now();
		cout << "TOP U TEST(SEC): " << chrono::duration_cast<chrono::seconds>(end - start).count() << endl;
		threads.clear();
		container.clear();
	}
	cout << "=========\tEND TEST 2\t=========" << endl;

	cout << "=========\tSTART TEST 3\t=========" << endl;
	{
		//1 поток -- передать n-1 элемент, уснуть, передать элемент
		//2 поток -- получить n элементов
		threads.emplace_back(delayedPutNElems, std::ref(container), N);
		threads.emplace_back(getNElems, std::ref(container), N);
		for (auto &thread : threads) {
			thread.join();
		}
		threads.clear();
		container.clear();
	}
	cout << "=========\tEND TEST 3\t=========" << endl;
*/
	cout << "=========\tSTART TEST 4\t=========" << endl;
	{
		//подать в map() throwFunction и чекнуть, что не дедлокнулось
		for (size_t i = 0; i < N; ++i) {
			if (i == 5) {
				threads.emplace_back(throwTest, ref(container));
			} else {
				threads.emplace_back(&Container<int>::put, std::ref(container), i);
				threads.emplace_back(noThrowTest, ref(container));
			}
		}
		for (auto &thread : threads) {
			thread.join();
		}
		threads.clear();
		container.clear();
	}
	cout << "=========\tEND TEST 4\t=========" << endl;

	return 0;
}
