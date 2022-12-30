#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <assert.h>

#include "cache.h"
#include "util.h"

#define CAPACITY 5
#define PUT "put\n"
#define GET "get\n"
#define END_SIGN "-\n"
#define PRINT "print\n"

int main()
{
    cache_t * cache = create_cache(CAPACITY);
    if (NULL == cache)
    {
        fprintf(stderr, "can't create cache\n");
        return -1;
    }

    char input[VALUE_LEN] = {'\0'};
    char buffer[VALUE_LEN] = {'\0'};
    long long int key = 0;

    while(true)
    {
        printf("enter the operation (put, get or print), '-' for exit\n");
        if (NULL == fgets(input, VALUE_LEN, stdin))
        {
            printf("can't read your input\n");
            continue;
        }

        if (0 == strcmp(input, PUT))
        {
            while(true)
            {
                printf("enter the key\n");
                bool read_num_ok = read_numbers(&key);
                if (!read_num_ok)
                {
                    printf("can't read the number\n");
                    skip();
                    continue;
                }
                printf("enter the value\n");
                if (NULL == fgets(buffer, VALUE_LEN, stdin))
                {
                    printf("can't read your input\n");
                    continue;
                }

                size_t len = strlen(buffer);
                if (buffer[len - 1] == '\n') // delete '\n' in the end
                {
                  buffer[len - 1] = '\0';
                }
                else
                {
                  printf("too long string\n");
                  skip();
                  continue;
                }

                cache_put(cache, key, buffer);

                printf("for exit from put enter '-', continue -- smth\n");
                if (NULL == fgets(buffer, VALUE_LEN, stdin))
                {
                    printf("can't read your input\n");
                    continue;
                }
                if (0 == strcmp(buffer, END_SIGN))
                {
                    break;
                }
            }
        }
        else if (0 == strcmp(input, GET))
        {
            while (true)
            {
                printf("enter the key\n");
                bool read_num_ok = read_numbers(&key);
                if (!read_num_ok)
                {
                    printf("can't read the number\n");
                    skip();
                    continue;
                }

                printf("%s\n", cache_get(cache, key));

                printf("for exit from put enter '-', continue -- smth \n");
                if (NULL == fgets(buffer, VALUE_LEN, stdin))
                {
                    printf("can't read your input\n");
                    continue;
                }
                if (0 == strcmp(buffer, END_SIGN))
                {
                    break;
                }
            }
        }
        else if (0 == strcmp(input, PRINT))
        {
            cache_print(cache);
        }
        else if (0 == strcmp(input, END_SIGN))
        {
            free_cache(cache);
            break;
        }
        else
        {
            printf("wrong input, try again\n");
            continue;
        }
    }
    return 0;
}
