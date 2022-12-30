#include <stdio.h>

#include "global_alignment.h"

char const * const messages[] =
{
	"Success",
	"First input error",
	"Second input error",
	"Can't create scoring matrix",
};



int main()
{
	printf("Enter 2 sequences\n");
	int result = align();
	printf("%s\n", messages[result]);
	return result;
}
