#include <stdio.h>

int prime_nubmer_X(unsigned int number) {
    int counter = 0;
    int temp = 0;

    for (int i = 2; ; i++) {
        for (int j = 1; j <= i; j++) {
            if (i % j == 0) {
                temp++;
            }
            if (temp > 2) {
                temp = 0;
                break;
            }
        }
        if (2 == temp) {
            counter++;
            temp = 0;
        }
        if (10001 == counter) {
            return i;
        }
    }
}

int main() {
    printf("%d", prime_nubmer_X(10001));
    return 0;
}
