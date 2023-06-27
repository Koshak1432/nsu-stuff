#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <stdbool.h>
#include <ctype.h>
#include <errno.h>
#include <sys/wait.h>

#define STOP_SIGN "."
#define CHILD_ID 0
#define NUM_PROCS 2
#define BUF_SIZE 1024

enum Errors {
	OK,
	E_PIPE_CREATION,
	E_FORK,
	E_WAITING,
	E_CHILD,
};

enum Errors RETURN_CODE = OK;

void handleString(char *str) {
	size_t len = strlen(str);
	if ('\n' == str[len - 1]) {
		str[len - 1] = '\0';
	}
}

void castToUpper(char *string) {
	size_t len = strlen(string);
	for (size_t i = 0; i < len; ++i) {
		string[i] = (char)toupper(string[i]);
	}
}

bool senderWork(int *fds) {
	if (-1 == close(fds[0])) {
		fprintf(stderr, "Sender 0 fd close error\n");
	}
	bool fail = false;
	char buf[BUF_SIZE + 1];
	memset(buf, '\0', BUF_SIZE);

	while (true) {
		if (NULL == fgets(buf, BUF_SIZE, stdin)) {
			fail = true;
			goto closeWriteFd;
		}
		handleString(buf);
		if (0 == strcmp(STOP_SIGN, buf)) {
			break;
		}
		while (true) {
			if (-1 == write(fds[1], buf, BUF_SIZE * sizeof(char))) {
				if (EINTR == errno) {
					continue;
				}
				perror("Couldn't write to another process");
				fail = true;
				goto closeWriteFd;
			}
			break;
		}
	}

closeWriteFd:
	if (-1 == close(fds[1])) {
		fprintf(stderr, "Sender 1 fd close error\n");
	}
	return !fail;
}

bool receiverWork(int *fds) {
	if (-1 == close(fds[1])) {
		fprintf(stderr, "Receiver 1 fd close error\n");
	}

	ssize_t readStatus;
	bool fail = false;
	char buf[BUF_SIZE + 1];
	memset(buf, '\0', BUF_SIZE);

	while (true) {
		while (true) {
			readStatus = read(fds[0], buf, BUF_SIZE * sizeof(char));
			if (-1 == readStatus) {
				if (EINTR == errno) {
					continue;
				}
				perror("Couldn't read line from another process");
				fail = true;
				goto closeReadFd;
			}
			break;
		}
		if (0 == readStatus) {
			break;
		}
		castToUpper(buf);
		printf("%s\n", buf);
	}

closeReadFd:
	if (-1 == close(fds[0])) {
		fprintf(stderr, "Receiver 0 fd close error\n");
	}
	return !fail;
}

int waitForProc() {
	int status = 0;
	if (-1 == wait(&status)) {
		perror("Waiting error");
		return E_WAITING;
	}
	if (!WIFEXITED(status)) {
		fprintf(stderr, "Child process with not normally terminated\n");
		return E_CHILD;
	}
	return OK;
}

int main() {
	printf("Enter up to %d symbols, %c to exit\n", BUF_SIZE - 1, STOP_SIGN);
	//[0] -- read, [1] -- write
	int fds[2];
	if (-1 == pipe(fds)) {
		perror("Couldn't create a pipe");
		RETURN_CODE = E_PIPE_CREATION;
		goto finish;
	}
	int childPid[NUM_PROCS];

	childPid[0] = fork();
	if (-1 == childPid[0]) {
		perror("Couldn't fork");
		RETURN_CODE = E_FORK;
		if (-1 == close(fds[0])) {
			fprintf(stderr, "0 fd close error\n");
		}
		if (-1 == close(fds[1])) {
			fprintf(stderr, "1 fd close error\n");
		}
		goto finish;
	}

	if (CHILD_ID == childPid[0]) {
		if (!senderWork(fds)) {
			fprintf(stderr, "Sender error\n");
			RETURN_CODE = E_CHILD;
		}
		goto finish;
	}

	childPid[1] = fork();
	if (-1 == childPid[1]) {
		perror("Couldn't fork");
		RETURN_CODE = E_FORK;
		if (-1 == close(fds[0])) {
			fprintf(stderr, "0 fd close error\n");
		}
		if (-1 == close(fds[1])) {
			fprintf(stderr, "1 fd close error\n");
		}
		goto finish;
	}

	if (CHILD_ID == childPid[1]) {
		if (!receiverWork(fds)) {
			fprintf(stderr, "Receiver error\n");
			RETURN_CODE = E_CHILD;
		}
		goto finish;
	}

	for (int i = 0; i < 2; ++i) {
		if (-1 == close(fds[i])) {
			fprintf(stderr, "Master %d fd close error\n", i);
		}
	}

	for (int i = 0; i < NUM_PROCS; ++i) {
		int status = waitForProc();
		if (OK != status) {
			RETURN_CODE = status;
			goto finish;
		}
	}

finish:
	return RETURN_CODE;
}

