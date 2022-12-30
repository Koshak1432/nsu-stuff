#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>

int main(int argc, char **argv) {
	if (argc < 2) {
		fprintf(stderr, "Gimme a prog to execute!\n");
		return -1;
	}
	int status = 0;
	pid_t id = fork();
	if (0 == id) { //child
		if (-1 == execvp(argv[1], argv + 1)) {
			perror("Exec fail in child process");
			return -2;
		}
	} else { //parent
		if (-1 == wait(&status)) {
			perror("Waiting error");
			return -3;
		}
		printf("\nHALLO BROTHA HOWAUDOIN?\n");
	}
	if (!WIFEXITED(status)) {
		fprintf(stderr, "Child process not normally terminated\n");
		return -4;
	} else {
		printf("Return code of the child: %d\n", WEXITSTATUS(status));
	}
	return 0;
}
