#include <stdio.h>
#include <assert.h>
#include <stdlib.h>
#include <string.h>

#include "list.h"

#define STR_LEN 1000
#define STOP_SIGN '.'

void handleString(char *str) {
	assert(NULL != str);

	size_t len = strlen(str);
	if ('\n' == str[len - 1]) {
		str[len - 1] = '\0';
	}
}

int main() {
	char *str = (char *)calloc(STR_LEN, sizeof(char));
	if (NULL == str) {
		fprintf(stderr, "Can't allocate buffer string\n");
		return -1;
	}
	list_t *list = NULL;

	while (1) {
		if (NULL == fgets(str, STR_LEN, stdin)) {
			fprintf(stderr, "Can't read line\n");
			free(str);
			freeList(list);
			return -2;
		}
		if (STOP_SIGN == str[0]) {
			free(str);
			break;
		}
		handleString(str); //delete \n in the end
		if (!insertNode(&list, str)) {
			fprintf(stderr, "Can't insert node\n");
			free(str);
			freeList(list);
			return -3;
		}
	}

	printf("\nStrings:\n");
	printList(list);
	freeList(list);
	return 0;
}
