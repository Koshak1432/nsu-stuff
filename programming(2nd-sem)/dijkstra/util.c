#include "util.h"

#include <stdlib.h>
#include <stdio.h>
#include <ctype.h>
#include <errno.h>

#define MAX_LEN 19 //В long long влезет до 9223372036854775807 (19 цифр)

bool read_numbers(long long int * result)
{
	char str[MAX_LEN + 1] = {'\0'}; //[19] == '\0'
	size_t len = 0;

	for (int i = 0; i < MAX_LEN; i++)
	{
		int ch = getchar();
		if (ch == EOF || !isdigit(ch))
		{
			str[i] = '\0';
			break;
		}
		str[i] = (char)ch;
		len++;
	}

	if (0 == len)
	{
		return true;
	}
	char * end;
	long long number = strtoll(str, &end, 10);

	if (end != &str[len] || errno == ERANGE)
	{
		return true;
	}

	*result = number;
	return false;
}
