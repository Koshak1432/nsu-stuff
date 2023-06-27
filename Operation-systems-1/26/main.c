#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>

enum Errors {
	OK = 0,
	E_EXEC,
	E_WAITING,
	E_STATUS,
};

int main() {
	int status = 0;
	pid_t id = fork();
	if (0 == id) {
		if (-1 == execl("sender", "sender", NULL)) {
			perror("Exec error");
			return E_EXEC;
		}
		return OK;
	}

	if (-1 == wait(&status)) {
		perror("Waiting error");
		return E_WAITING;
	}
	if (!WIFEXITED(status)) {
		fprintf(stderr, "Child process not normally terminated\n");
		return E_STATUS;
	}


	return OK;
}
