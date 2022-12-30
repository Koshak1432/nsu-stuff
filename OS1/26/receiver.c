#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <errno.h>
#include <ctype.h>

#include "const.h"

enum Errors {
	OK = 0,
	E_FGETS,
};

void castToUpper(char *string) {
	size_t len = strlen(string);
	for (size_t i = 0; i < len; ++i) {
		string[i] = (char)toupper(string[i]);
	}
}

void handleString(char *str) {
	size_t len = strlen(str);
	if ('\n' == str[len - 1]) {
		str[len - 1] = '\0';
	}
}

int main() {
	char buf[BUF_SIZE + 1];
	memset(buf, '\0', BUF_SIZE);

	while (true) {
		while (true) {
			if (NULL == fgets(buf, BUF_SIZE, stdin)) {
				if (EINTR == errno) {
					continue;
				}
				perror("Couldn't get data from stdin");
				return E_FGETS;
			}
			break;
		}
		if (0 == strcmp(buf, STOP_SIGN)) {
			break;
		}
		handleString(buf);
		castToUpper(buf);
		printf("%s\n", buf);
	}

	return OK;
}