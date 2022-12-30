#include <stdio.h>
#include <string.h>
#include <assert.h>

#include "sort.h"
#include "read_and_put.h"

int cmp_inc(void const *first, void const *second)
{
    assert(NULL != first);
    assert(NULL != second);
    int arg1 = *(int const *)first;
    int arg2 = *(int const *)second;
    if (arg1 > arg2)
    {
        return 1;
    }
    else if (arg1 < arg2)
    {
        return -1;
    }
    return 0;
}

int cmp_dec(void const *first, void const *second)
{
    assert(NULL != first);
    assert(NULL != second);
    int arg1 = *(int const *)first;
    int arg2 = *(int const *)second;
    if (arg1 > arg2)
    {
        return -1;
    }
    else if (arg1 < arg2)
    {
        return 1;
    }
    return 0;
}

int main(int argc, char *argv[])
{
    if (argc < 3)
    {
        fprintf(stderr, "files and key not found\n");
        return -1;
    }
    int error = 0;
    int count_files = 0;
    char key = 'k';

    if (strcmp(argv[2], "+") == 0)
    {
        key = '+';
    }
    else if (strcmp(argv[2], "-") == 0)
    {
        key = '-';
    }
    else
    {
        fprintf(stderr, "wrong key, must be '+' for increase, '-' for decrease\n");
        return -4;
    }

    FILE *f1 = fopen(argv[1], "r"); // this is the main file
    if (NULL == f1)
    {
        fprintf(stderr, "can't open the main(your) file %s\n", argv[1]);
        return -3;
    }

    make_sorted_files(&count_files, &error, 7, f1, (key == '+') ? cmp_inc : cmp_dec);
    if (error < 0)
    {
        fprintf(stderr, "error in partition of files\n");
        return -2;
    }

    fclose(f1);
    make_result(count_files, &error, (key == '+') ? cmp_inc : cmp_dec);
    if (error < 0)
    {
        fprintf(stderr, "error in making result file\n");
        return -7;
    }

    fprintf(stdout, "SUCCESSFULLY!\n");
    return 0;
}
