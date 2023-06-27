#include <stdio.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <ctype.h>
#include <sys/poll.h>
#include <errno.h>

#define MAX_CLIENTS 5
#define BUF_SIZE 100

enum Errors {
	OK = 0,
	E_SOCKET,
	E_BIND,
	E_LISTEN,
	E_ACCEPT,
	E_READING,
	E_POLL,
};

void castToUpper(char *string, size_t len) {
	for (size_t i = 0; i < len; ++i) {
		string[i] = (char)toupper(string[i]);
	}
}

int acceptConnection(int serverSocket, int *clientSocket) {
	while (1) {
		if (-1 == (*clientSocket = accept(serverSocket, NULL, NULL))) {
			if (EINTR == errno) {
				continue;
			}
			perror("Accept error");
			return E_ACCEPT;
		}
		break;
	}
	return OK;
}

struct sockaddr_un getSocketAddr(const char *socketPath) {
	struct sockaddr_un addr;
	memset(&addr, 0, sizeof(addr));
	addr.sun_family = AF_UNIX;
	strncpy(addr.sun_path, socketPath, sizeof(addr.sun_path) - 1);
	return addr;
}

void addClientToPoll(struct pollfd *pollfd, int clientSocket) {
	pollfd->fd = clientSocket;
	pollfd->events = POLLIN;
	pollfd->revents = 0;
}

int addClient(struct pollfd *whence, int serverSocket, nfds_t *clientsNum) {
	int newClient;
	enum Errors RETURN_CODE;
	if (OK != (RETURN_CODE = acceptConnection(serverSocket, &newClient))) {
		return RETURN_CODE;
	}

	if (MAX_CLIENTS == *clientsNum) {
		fprintf(stderr,"Clients limit is reached\n");
		close(newClient);
		return OK;
	}
	addClientToPoll(whence, newClient);
	++(*clientsNum);
	return OK;
}

void closeClient(struct pollfd *pollfd) {
	if (-1 == close(pollfd->fd)) {
		fprintf(stderr, "Client closing error\n");
	}
}

void closeAllClients(struct pollfd *fds, nfds_t nfds) {
	for (nfds_t i = 0; i < nfds; ++i) {
		if (fds[i].fd > 0) {
			closeClient(&fds[i]);
		}
	}
}

int handleClient(struct pollfd *pollfd, nfds_t *clientsNum) {
	ssize_t readNum;
	char buf[BUF_SIZE];
	memset(buf, '\0', BUF_SIZE * sizeof(buf[0]));

	while ((readNum = recv(pollfd->fd, buf, (BUF_SIZE - 1) * sizeof(buf[0]), MSG_DONTWAIT)) > 0) {
		buf[readNum - 1] = '\0';
		castToUpper(buf, readNum);
		printf("%s\n", buf);
	}
	if (-1 == readNum) {
		if (EAGAIN == errno) {
			return OK;
		}
		perror("Read error");
		return E_READING;
	}

	if (0 == readNum) {
		printf("Closing client with fd %d\n", pollfd->fd);
		closeClient(pollfd);
		pollfd->fd *= -1; //skip next time
		--(*clientsNum);
	}
	return OK;
}

int main() {
	const char *socketPath = "/mnt/c/Users/sadri/OS/lab30/serverSocket";
	enum Errors RETURN_CODE = OK;
	int serverSocket, pollStatus;
	struct pollfd fds[MAX_CLIENTS + 1];
	nfds_t nfds = 1; //server
	nfds_t clientsNum = 0;

	if (-1 == (serverSocket = socket(AF_UNIX, SOCK_STREAM, 0))) {
		perror("Socket error");
		RETURN_CODE = E_SOCKET;
		goto finish;
	}
	struct sockaddr_un addr = getSocketAddr(socketPath);

	if (-1 == bind(serverSocket, (struct sockaddr*)&addr, sizeof(addr))) {
		perror("Bind error");
		RETURN_CODE = E_BIND;
		goto closing;
	}
	if (-1 == listen(serverSocket, MAX_CLIENTS)) {
		perror("Listen error");
		RETURN_CODE = E_LISTEN;
		goto closing;
	}
	addClientToPoll(&fds[0], serverSocket);

	printf("Waiting to accept a connection...\n");
	while (1) {
		pollStatus = poll(fds, nfds, -1);
		if (-1 == pollStatus) {
			if (EINTR == errno) {
				continue;
			}
			perror("Poll error");
			RETURN_CODE = E_POLL;
			goto closing;
		}

		for (nfds_t fd = 0; fd < nfds; ++fd) {
			//ignoring negatives
			if (fds[fd].fd < 0) {
				continue;
			}
			//fd is ready for reading
			if (POLLIN == (fds[fd].revents & POLLIN)) {
				if (serverSocket == fds[fd].fd) {
					if (OK != (RETURN_CODE = addClient(&fds[nfds++], serverSocket, &clientsNum))) {
						goto closing;
					}
					printf("Added new client\n");
				} else {
					if (OK != (RETURN_CODE = handleClient(&fds[fd], &clientsNum))) {
						goto closing;
					}
				}
			}
			if (0 == clientsNum) {
				printf("All clients are gone, closing the server\n");
				goto closing;
			}
		}
	}

closing:
	closeAllClients(fds, nfds);
	if (-1 == unlink(socketPath)) {
		perror("Unlink error");
	}

finish:
	return RETURN_CODE;
}