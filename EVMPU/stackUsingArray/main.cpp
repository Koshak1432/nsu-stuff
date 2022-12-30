#include <iostream>

namespace
{
	constexpr std::size_t DEFAULT_CAPACITY = 100;
	constexpr std::size_t MULTIPLY_FAC = 2;

}

template <class T>
class Stack
{
public:
	explicit Stack(std::size_t capacity = DEFAULT_CAPACITY) : data_(new T[capacity]), capacity_(capacity){};
	Stack(const Stack<T> &other);
	~Stack();

	bool empty();
	std::size_t size();
	void swap(Stack<T> &other);
	T &top();

	void pushBack(const T &el);
	void pushBack(T &&el);
	void resize(std::size_t newCapacity);

	Stack<T> &operator =(const Stack<T> &other);


private:
	T *data_ = nullptr;
	std::size_t capacity_ = DEFAULT_CAPACITY;
	std::size_t size_ = 0;
};

//template<class T>
//Stack<T>::Stack(std::size_t capacity) : data_(new T[capacity]), capacity_(capacity)
//{}

template<class T>
bool Stack<T>::empty()
{
	return 0 == size_;
}

template<class T>
std::size_t Stack<T>::size()
{
	return size_;
}

template<class T>
T &Stack<T>::top()
{
	if (!empty())
	{
		return data_[size_ - 1];
	}
	throw std::out_of_range("Top method in empty stack");
}

template<class T>
void Stack<T>::pushBack(const T &el)
{
	if (size_ == capacity_)
	{
		resize(capacity_ * MULTIPLY_FAC);
	}
	data_[size_++] = el;
}

template<class T>
void Stack<T>::pushBack(T &&elem)
{
	if (size_ == capacity_)
	{
		resize(capacity_ * MULTIPLY_FAC);
	}
	data_[size_++] = std::move(elem);
}

template<class T>
void Stack<T>::swap(Stack<T> &other)
{
	std::swap(data_, other.data_);
	std::swap(size_, other.size_);
	std::swap(capacity_, other.capacity_);
}

template<class T>
Stack<T>::~Stack()
{
	delete []data_;
}

template<class T>
Stack<T> &Stack<T>::operator =(const Stack<T> &other)
{
	if (this != &other)
	{
		delete[] data_;
		capacity_ = other.capacity_;
		size_ = other.size_;
		data_ = new T[other.capacity_];
		std::copy(other.data_, other.data_ + other.size_, data_);
	}
	return *this;
}

template<class T>
Stack<T>::Stack(const Stack<T> &other) : data_(new T[other.capacity_]), capacity_(other.capacity_), size_(other.size_)
{
	std::copy(other.data_, other.data_ + other.size_, data_);
}

template<class T>
void Stack<T>::resize(std::size_t newCapacity)
{
	T *tmp = new T[newCapacity];
	std::copy(data_, data_ + size_, tmp);
	delete[] data_;
}

int main()
{
	std::cout << "Hello, World!" << std::endl;
	return 0;
}
