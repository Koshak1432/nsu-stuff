#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <string.h>

#define EPOCH_YR 1900
#define BUF_SIZE 50

int main() {
	time_t now;
	struct tm sp;
	char buf[BUF_SIZE];

	if (0 != setenv("TZ", "PST8PDT", 1)) {
		perror("Can't set TZ environment variable");
		return -1;
	}

	if ((time_t)-1 == time(&now)) {
		perror("Can't save time in variable");
		return -2;
	}

	if (NULL == localtime_r(&now, &sp)) {
		perror("Can't get tm structure");
		return -3;
	}

	if (NULL == asctime_r(&sp, buf)) {
		perror("Can't convert time to a string");
		return -4;
	}

	printf("%s", buf);
	printf("%02d/%02d/%02d %02d:%02d:%02d\n",
	       sp.tm_mon + 1, sp.tm_mday, sp.tm_year + EPOCH_YR,
	       sp.tm_hour, sp.tm_min, sp.tm_sec);

	return 0;
}