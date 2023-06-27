#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <errno.h>

#include "const.h"

#define USAGE(SIZE) printf("Enter up to %d symbols, . to exit\n", (SIZE))

enum Errors {
	OK = 0,
	E_POPEN,
	E_FGETS,
	E_FPUTS,
	E_CLOSE,
	E_STATUS,
};

 int main() {
	enum Errors RETURN_CODE = OK;
	FILE *receiver = popen("./receiver", "w");
	if (NULL == receiver) {
		perror("Couldn't create receiver stream");
		RETURN_CODE = E_POPEN;
		goto finish;
	}

	USAGE(BUF_SIZE - 1);

	int status = 0;
	char buf[BUF_SIZE + 1];
	memset(buf, '\0', BUF_SIZE);

	while (true) {
		while (true) {
			if (NULL == fgets(buf, BUF_SIZE, stdin)) {
				if (EINTR == errno) {
					continue;
				}
				perror("Couldn't get data from stdin");
				RETURN_CODE = E_FGETS;
				goto waiting;
			}
			break;
		}
		while (true) {
			if (EOF == fputs(buf, receiver)) {
				if (EINTR == errno) {
					continue;
				}
				perror("Couldn't send data to the receiver");
				RETURN_CODE = E_FPUTS;
				goto waiting;
			}
			fflush(receiver);
			if (0 == strcmp(STOP_SIGN, buf)) {
				goto waiting;
			}
			break;
		}
	}

waiting:
	status = pclose(receiver);
	if (-1 == status) {
		perror("Receiver close error");
		RETURN_CODE = E_CLOSE;
	} else if (0 != status) {
		fprintf(stderr, "Receiver status is %d\n", status);
		RETURN_CODE = E_STATUS;
	}
finish:
	return RETURN_CODE;
}
