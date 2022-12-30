#include <stdio.h>
#include <unistd.h>

int main(int argc, char **argv) {
	if (argc != 2) {
		fprintf(stderr, "Gimme da file!\n");
		return -1;
	}

	char *path = argv[1];
	printf("Real UID: %u, Effective UID: %u\n", getuid(), geteuid());

	FILE *file = fopen(path, "r+");
	if (NULL == file) {
		perror("(1)Can't open file");
	}
	if (0 != seteuid(getuid())) {
		perror("Can't set effective uid");
	}

	FILE *file2 = fopen(path, "r+");
	if (NULL == file2) {
		perror("(2)Can't open file");
	}
	printf("Real UID: %u, Effective UID: %u\n", getuid(), geteuid());

	if (NULL != file) {
		fclose(file);
	}
	if (NULL != file2) {
		fclose(file2);
	}

	return 0;
}
