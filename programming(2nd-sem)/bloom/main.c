#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <stdint.h>
#include <string.h>
#include <stdbool.h>
#include <ctype.h>
#include <errno.h>

#define BITS_IN_BYTE 8
#define STRING_MAX_SIZE 50
#define END_SIGN "-\n"

typedef struct
{
    size_t hash_count;
    size_t field_size; //in bits
    uint64_t *field;
    size_t *arr_digits;
    size_t (*hash)(char const *, size_t, size_t);
} bloom_filter_t;

enum Errors
{
    OK,
    WITHOUT_FILE_ERROR,
    OPEN_FILE_ERROR,
    ZERO_HASH_FUNC_ERROR,
    MEMORY_ALLOCATE_ERROR,
    READING_STRINGS_FROM_FILE_ERROR,
    READING_STRING_ERROR,
    SCANF_ERROR,
    CREATE_ARR_DIGITS_ERROR,
    NUMBERS_READING_ERROR
};

size_t make_prime_digit(size_t number)
{
    size_t *tmp = (size_t *)calloc(number * number * 2, sizeof(*tmp));

    for (size_t i = 0; i < number * number * 2; i++)
    {
        tmp[i] = i;
    }

    size_t i = 2;
    tmp[1] = 0;

    for (; i * i < number * number * 2; i++)
    {
        if (tmp[i] != 0)
        {
            for (size_t k = i * i; k < number * number * 2; k += i)
            {
                tmp[k]=0;
            }
        }
    }
    size_t count = 0;

    for (i = 0; i < number * number * 2; i++)
    {
        if (tmp[i] != 0)
        {
            count++;
        }
        if (count == number)
        {
            break;
        }
    }
    size_t result = tmp[i];
    free(tmp);
    return result;
}

static size_t hash(char const * name, size_t prime_number, size_t len)
{
    assert(NULL != name);

    size_t strl = strlen(name);
    size_t res = 0;

    for (size_t i = 0; i < strl; i++)
    {
        res += name[i] * name[i] + prime_number;
        res %= len;
    }
    return res;
}

static bloom_filter_t *create_filter(size_t field_size, size_t hash_count)
{
    assert(field_size > 0);
    assert(hash_count > 0);

    bloom_filter_t * filter = (bloom_filter_t *)calloc(1, sizeof(*filter));
    if (NULL == filter)
    {
        return NULL;
    }
    filter->field = (uint64_t *)calloc(field_size / BITS_IN_BYTE + 1, sizeof(*filter->field));
    if (NULL == filter->field)
    {
        free(filter);
        return NULL;
    }
    filter->hash = hash;
    filter->field_size = field_size;
    filter->hash_count = hash_count;
    return filter;
}

static void free_filter(bloom_filter_t * filter)
{
    assert(NULL != filter);
    free(filter->arr_digits);
    free(filter->field);
    free(filter);
}



static void add_elem(bloom_filter_t * filter, char const * key)
{
    assert(NULL != filter);
    assert(NULL != key);

    for (size_t i = 0; i < filter->hash_count; i++)
    {
        size_t hash_t = filter->hash(key, filter->arr_digits[i], filter->field_size);
        uint64_t byte = hash_t / BITS_IN_BYTE;
        uint8_t bit = hash_t % BITS_IN_BYTE;

        filter->field[byte] |= 1 << (BITS_IN_BYTE - bit);
    }
}

static bool check_elem(bloom_filter_t * filter, char const * key)
{
    assert(NULL != filter);
    assert(NULL != key);

    for (size_t i = 0; i < filter->hash_count; i++)
    {
        size_t hash_t = filter->hash(key, filter->arr_digits[i], filter->field_size);
        uint64_t byte = hash_t / BITS_IN_BYTE;
        uint8_t bit = hash_t % BITS_IN_BYTE;
        if ((filter->field[byte] >> (BITS_IN_BYTE - bit) & 1) == 0)
        {
            return false;
        }
    }
    return true;
}

static bool add_strings_from_file(bloom_filter_t * filter, FILE * file)
{
    assert(NULL != filter);
    assert(NULL != file);

    char buffer[STRING_MAX_SIZE] = {0};

    while(!feof(file))
    {
        size_t idx = 0;
        while (1)
        {
            int ch = fgetc(file);
            if (isalpha(ch))
            {
                buffer[idx] = (char)ch;
                idx++;
            }
            else
            {
                add_elem(filter, buffer);
                memset(buffer, '\0', STRING_MAX_SIZE);
                break;
            }
        }
    }
    return true;
}

static void skip()
{
    while(1)
    {
        int ch = getchar();
        if (EOF == ch || '\n' == ch)
        {
            return;
        }
    }
}

static bool create_arr_digits(bloom_filter_t *filter)
{
    assert(NULL != filter);

    filter->arr_digits = (size_t *)calloc(filter->hash_count, sizeof(*filter->arr_digits));
    if (NULL == filter->arr_digits)
    {
        return false;
    }

    for (size_t i = 1; i <= filter->hash_count; i++)
    {
        filter->arr_digits[i] = make_prime_digit(100 * i);
    }
    return true;
}

static bool read_numbers(size_t * result)
{
    //В unsigned long long влезет до 18446744073709551615 (20 цифр)
    char str[21] = {'\0'}; //[20] == '\0'
    size_t len = 0;

    for (int i = 0; i < 20; i++)
    {
        int ch = fgetchar();
        if (ch == EOF || !isdigit(ch))
        {
            str[i] = '\0';
            break;
        }
        str[i] = (char)ch;
        len++;
    }

    if (0 == len)
    {
        return false;
    }
    char *end;
    unsigned long long number = strtoull(str, &end, 10);

    if (end != &str[len] || errno == ERANGE)
    {
        return false;
    }

    *result = (size_t)number;
    return true;
}

int main(int argc, char *argv[])
{
    if (argc < 2)
    {
        fprintf(stderr, "Can't find the file\n");
        return WITHOUT_FILE_ERROR;
    }
    FILE * file = fopen(argv[1], "r");
    if (NULL == file)
    {
        fprintf(stderr, "Can't open this file\n");
        return OPEN_FILE_ERROR;
    }

    size_t field_size = 0;
    printf("Enter a field size\n");

    if(!read_numbers(&field_size))
    {
        fprintf(stderr, "can't read numbers\n");
        return NUMBERS_READING_ERROR;
    }

    size_t hash_numbers = 0;
    printf("Enter a number of hash\n");
    if (!read_numbers(&hash_numbers))
    {
        return NUMBERS_READING_ERROR;
    }
    if (0 == hash_numbers)
    {
        fprintf(stderr, "can't run prog without hash_func\n");
        return ZERO_HASH_FUNC_ERROR;
    }

    bloom_filter_t *filter = create_filter(field_size, hash_numbers);
    if (NULL == filter)
    {
        fprintf(stderr, "Can't allocate memory for a filter.\n");
        return MEMORY_ALLOCATE_ERROR;
    }

    if (!create_arr_digits(filter))
    {
        return CREATE_ARR_DIGITS_ERROR;
    }

    if (!add_strings_from_file(filter, file))
    {
        fprintf(stderr, "Can't read strings from the file\n");
        return READING_STRINGS_FROM_FILE_ERROR;
    }

    fclose(file);
    char string[STRING_MAX_SIZE] = {'\0'};
    memset(string, '\0', STRING_MAX_SIZE);
    skip();

    while(true)
    {
        printf("Please, input a string. Send '-' for exit.\n");
        if (NULL == fgets(string, STRING_MAX_SIZE, stdin)) // +1 to make sting without \n(if too long)
        {
            fprintf(stderr, "Can't read the string\n");
            return READING_STRING_ERROR;
        }
        size_t len = strlen(string);

        if (string[len - 1] == '\n') // delete '\n' in the end
        {
            string[len - 1] = '\0';
        }

        if (strcmp(END_SIGN, string) == 0)
        {
            break;
        }

        if (string[STRING_MAX_SIZE - 1] != '\0')
        {
            skip();
            printf("the string is too large, must be up to %d symbols(CAN BE CHANGED)\n",
                    STRING_MAX_SIZE);
            memset(string, '\0', STRING_MAX_SIZE);
            continue;
        }

        if (check_elem(filter, string))
        {
            fprintf(stdout, "%s | maybe is here\n", string);
        }
        else
        {
            fprintf(stdout, "%s | definitely isn't here\n", string);
        }
        memset(string, '\0', STRING_MAX_SIZE);
    }
    free_filter(filter);
    return OK;
}
