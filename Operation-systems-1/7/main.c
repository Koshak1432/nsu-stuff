#include <assert.h>
#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/poll.h>
#include <stdbool.h>
#include <sys/mman.h>
#include <errno.h>

#define NUM_CHUNKS 50
#define STR_LEN 60
#define TIMEOUT_MSECS 5000

typedef struct {
	size_t endIdx;
	size_t len;
} chunk_t;

typedef struct {
	size_t size;
	size_t chunksCapacity;
	chunk_t *chunks;
} chunksTable_t;

enum Errors {
	OK,
	NO_ARGUMENTS_ERROR,
	OPEN_FILE_ERROR,
	FILE_SIZE_ERROR,
	BUFFER_CREATION_ERROR,
	TABLE_CREATION_ERROR,
	CLOSE_ERROR,
	READ_ERROR,
	POLL_ERROR
};

enum Errors RETURN_CODE = OK;
bool FISOPENED = false;

off_t getFileSize(int fd) {
	struct stat s;
	if (-1 == fstat(fd, &s)) {
		return -1;
	}
	return s.st_size;
}

chunksTable_t *getChunksTable(char *fileBuffer) {
	assert(NULL != fileBuffer);
	chunksTable_t *table = malloc(sizeof(chunksTable_t));
	if (NULL == table) {
		return NULL;
	}
	table->chunksCapacity = NUM_CHUNKS;
	table->size = 0;
	table->chunks = malloc(table->chunksCapacity * sizeof(chunk_t));
	if (NULL == table->chunks) {
		free(table);
		return NULL;
	}
	memset(table->chunks, '\0', table->chunksCapacity * sizeof(chunk_t));
	size_t chunkIdx = 0;
	size_t len = 0;
	size_t bufferSize = strlen(fileBuffer) + 1;

	for (size_t fileIdx = 0; fileIdx < bufferSize; ++fileIdx) {
		char symbol = fileBuffer[fileIdx];
		if ('\n' == symbol || bufferSize - 1 == fileIdx || STR_LEN == len) {
			if (chunkIdx == table->chunksCapacity - 1) {
				table->chunksCapacity *= 2;
				table->chunks = realloc(table->chunks, table->chunksCapacity * sizeof(chunk_t));
				if (NULL == table->chunks) {
					free(table);
					return NULL;
				}
			}
			table->chunks[chunkIdx].endIdx = fileIdx;
			table->chunks[chunkIdx].len = len;
			len = 0;
			++chunkIdx;
		} else {
			++len;
		}
	}
	table->size = chunkIdx;
	return table;
}

void releaseResources(chunksTable_t *table, char *fileBuffer, size_t size) {
	if (NULL != table) {
		if (NULL != table->chunks) {
			free(table->chunks);
		}
		free(table);
	}
	if (NULL != fileBuffer) {
		if (-1 == munmap(fileBuffer, size)) {
			perror("Unmap error");
		}
	}
}

int main(int argc, char **argv) {
	chunksTable_t *chunksTable = NULL;
	char *fileBuffer = NULL;
	off_t fileSize = 0;

	if (argc < 2) {
		fprintf(stderr, "Gimme a file\n");
		RETURN_CODE = NO_ARGUMENTS_ERROR;
		goto ERROR_HANDLING;
	}
	int fd;
	while (true) {
		fd = open(argv[1], O_RDONLY);
		if (-1 == fd) {
			if (EINTR == errno) {
				continue;
			}
			perror("Couldn't open the file");
			RETURN_CODE = OPEN_FILE_ERROR;
			goto ERROR_HANDLING;
		}
		FISOPENED = true;
		break;
	}
	fileSize = getFileSize(fd);
	if (fileSize < 0) {
		perror("Invalid file size");
		RETURN_CODE = FILE_SIZE_ERROR;
		goto ERROR_HANDLING;
	}
	fileBuffer = mmap(NULL, fileSize, PROT_READ, MAP_PRIVATE, fd, 0);
	if (NULL == fileBuffer || MAP_FAILED == fileBuffer) {
		perror("Couldn't mmap the file");
		RETURN_CODE = BUFFER_CREATION_ERROR;
		goto ERROR_HANDLING;
	}
	chunksTable = getChunksTable(fileBuffer);
	if (NULL == chunksTable) {
		perror("Couldn't create chunks table");
		RETURN_CODE = TABLE_CREATION_ERROR;
		goto ERROR_HANDLING;
	}
	char string[STR_LEN + 1];
	struct pollfd fds;
	fds.fd = STDIN_FILENO;
	fds.events = POLLIN;

	while (true) {
		printf("Please, enter an idx\n");
		int idx = 0;
		int ready = poll(&fds, 1, TIMEOUT_MSECS);
		while (true) {
			ready = poll(&fds, 1, TIMEOUT_MSECS);
			if (0 == ready) {
				printf("\nThe content of file:\n%s\n", fileBuffer);
				break;
			} else if (-1 == ready) {
				if (EINTR == errno) {
					continue;
				}
				perror("Poll error");
				RETURN_CODE = POLL_ERROR;
				goto ERROR_HANDLING;
			}
			break;
		}

		if (1 != fscanf(stdin, "%d", &idx)) {
			fprintf(stderr, "Invalid idx: not a number\n");
			if (-1 == tcflush(STDIN_FILENO, TCIFLUSH)) {
				perror("Can't clear the terminal");
				RETURN_CODE = READ_ERROR;
				goto ERROR_HANDLING;
			}
			continue;
		}

		if (0 == idx) {
			break;
		}
		if (idx < 0 || idx > chunksTable->size) {
			fprintf(stderr, "Invalid idx: available idxs are in range [%d, %zu]\n", 1, chunksTable->size);
			if (-1 == tcflush(STDIN_FILENO, TCIFLUSH)) {
				perror("Can't clear the terminal");
				RETURN_CODE = READ_ERROR;
				goto ERROR_HANDLING;
			}
			continue;
		}
		chunk_t chunk = chunksTable->chunks[idx - 1];
		strncpy(string, fileBuffer + chunk.endIdx - chunk.len, chunk.len);
		string[chunk.len] = '\0';
		printf("%s\n", string);
	}

	ERROR_HANDLING:
	releaseResources(chunksTable, fileBuffer, fileSize);
	if (FISOPENED) {
		while (true) {
			if (-1 == close(fd)) {
				if (EINTR == errno) {
					continue;
				}
				perror("Couldn't close the file");
				return CLOSE_ERROR + RETURN_CODE;
			}
			break;
		}
	}
	return RETURN_CODE;
}
