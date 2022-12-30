#include <stdio.h>

typedef unsigned long long int u_type;
double check_one(u_type num)
{
	return (num % 2 == 0) ? 1.0 : -1.0;
}

double count_pi(u_type num)
{
	double res = 0;
	for (u_type i = 0; i < num; ++i)
	{
		res += 4.0 * check_one(i) / (2 * i + 1);
	}
	return res;
}

int main()
{
	u_type N = 9000000000;
	printf("result : %.20lf\n", count_pi(N));
	return 0;
}