#include <sys/socket.h>
#include <sys/un.h>
#include <stdio.h>
#include <unistd.h>
#include <errno.h>

#define BUF_SIZE 101

#define USAGE(SIZE) printf("Enter up to %d symbols, . to exit\n", (SIZE))
#define STOP_SIGN ".\n"

enum Errors {
	OK = 0,
	E_SOCKET,
	E_CONNECT,
	E_WRITING,
	E_READING,
};

struct sockaddr_un getSocketAddr(const char *socketPath) {
	struct sockaddr_un addr;
	memset(&addr, 0, sizeof(addr));
	addr.sun_family = AF_UNIX;
	strncpy(addr.sun_path, socketPath, sizeof(addr.sun_path) - 1);
	return addr;
}

int main() {
	const char *socketPath = "/mnt/c/Users/sadri/OS/lab30/serverSocket";
	enum Errors RETURN_CODE = OK;
	char buf[BUF_SIZE];
	int clientSocket;
	size_t len  = 0;
	memset(buf, '\0', BUF_SIZE * sizeof(buf[0]));

	if (-1 == (clientSocket = socket(AF_UNIX, SOCK_STREAM, 0))) {
		perror("Socket error");
		RETURN_CODE = E_SOCKET;
		goto finish;
	}

	struct sockaddr_un addr = getSocketAddr(socketPath);
	while (1) {
		if (-1 == connect(clientSocket, (struct sockaddr*)&addr, sizeof(addr))) {
			if (EINTR == errno) {
				continue;
			}
			perror("Connect error");
			RETURN_CODE = E_CONNECT;
			goto closing;
		}
		break;
	}

	USAGE(BUF_SIZE - 1);

	while(1) {
		while (1) {
			if (NULL == fgets(buf, BUF_SIZE, stdin)) {
				if (EINTR == errno) {
					continue;
				}
				perror("Couldn't get data from stdin");
				RETURN_CODE = E_READING;
				goto closing;
			}
			break;
		}
		if (0 == strcmp(buf, STOP_SIGN)) {
			break;
		}
		len = strlen(buf);
		while (1) {
			if (-1 == write(clientSocket, buf, len * sizeof(buf[0]))) {
				if (EINTR == errno) {
					continue;
				}
				perror("Writing error");
				RETURN_CODE = E_WRITING;
				goto closing;
			}
			break;
		}
	}

closing:
	if (-1 == close(clientSocket)) {
		fprintf(stderr, "Close error\n");
	}
finish:
	return RETURN_CODE;
}