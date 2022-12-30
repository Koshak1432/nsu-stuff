#include "sort.h"
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>


#define MAX_FILES 10

static void *find_MinMax(void *arr, size_t size, size_t elem_size,
               int(*cmp)(void const *, void const *), size_t * position)
{
    assert(NULL != arr);
    void *res = arr;
    char *arr_chars = (char *)arr;

    for (size_t i = 0; i < size; i++)
    {
        if (cmp(res, &arr_chars[i * elem_size]) > 0)
        {
            res = &arr_chars[i * elem_size];
            *position = i;
        }
    }
    return res;
}

static void remove_from_arr(void *arr, size_t idx, int len, size_t elem_size)
{
    assert(NULL != arr);
    char *arr_chars = (char*)arr + (idx * elem_size);  // (idx * elem_size)- сколько байтов слева от нужного
    for (size_t i = 0; i < (len - idx - 1) * elem_size; i++) //копирую elem_size байтов
                    //последний элемент копируется в предпоследний(a[i] = a[i + 1] побайтово)
    {
        arr_chars[i] = arr_chars[i + elem_size]; // arr[0] = arr[0 + elem_size]
                           //поочерёдно меняю байты одного элемента с байтами другого,
                           //между нужным байтом и текущим расстояние elem_size
    }
}

void make_sorted_files(int *count_f, int *error, int how_many_in_1_file, FILE * mainf,
                    int(*cmp)(void const *, void const *))
{
    int i = 0; // using for reading from the file
    int start = 0;
    int check_scan = 1;
    int *a = (int*)calloc(MAX_FILES * how_many_in_1_file + 1, sizeof(*a));

    while (check_scan == 1)
    {
        size_t len = snprintf(NULL, 0, "file-%d.txt", *count_f); // count length of current file
        char *name = (char*)calloc(len + 1, sizeof(*name)); //a string for name of file,  +1 for \0
        snprintf(name, len + 1, "file-%d.txt", *count_f); // print in name name of file
        FILE *f = fopen(name, "w"); // create new file
        if (NULL == f)
        {
            fprintf(stderr, "problems with opening file %s\n", name);
            (*error)--;
            free(name);
            return;
        }
        while (1)
        {
            if (fscanf(mainf, "%d", &a[i++]) != 1)
            {
                check_scan = 0;
                i--; //потому что i увеличился
                break;
            }
            if ((i % how_many_in_1_file) == 0) //если в файле записано максимально возможное кол-во интов
            {
                (*count_f)++;
                break;
            }
        }
        sort(a, start, i - 1, cmp);

        for (int j = start; j < i; j++)
        {
            fprintf(f, "%d ", a[j]);
        }
        start += how_many_in_1_file;
        if (*count_f > MAX_FILES)
        {
            fprintf(stderr, "TOO MANY FILES\n");
            (*error)--;
            fclose(f);
            free(name);
            return;
        }
        fclose(f);
        free(name);
    }
    free(a);
}

void make_result(int count_files, int *err, int (*cmp)(const void *, const void *))
{
    FILE **files = (FILE**)calloc(count_files + 1, sizeof(FILE*));  //5 файлов -> count_files == 4
    size_t len = snprintf(NULL, 0, "file-%d.txt", count_files);
    char *file_name = (char*)calloc(len + 1, sizeof(*file_name));
    for (int i = 0; i <= count_files; i++) // open all files
    {
        snprintf(file_name, len + 1, "file-%d.txt", i);
        files[i] = fopen(file_name, "r");
        if (NULL == files[i])
        {
            fprintf(stderr, "can't open file %s\n", file_name);
            (*err)--;
            for (int j = i - 1; j > -1; j--)
            {
                fclose(files[j]);
            }
            free(file_name);
            return;
        }
    }
    free(file_name);

    //записываю первые числа и сортирую
    len = count_files + 1;
    FILE *f = fopen("result.txt", "w");
    int *first_items = (int *)calloc(len, sizeof(*first_items));
    for (size_t i = 0; i < len; i++)
    {
        if(fscanf(files[i], "%d", &first_items[i]) != 1)      //if main file is empty
        {
            fclose(f);
            fclose(files[i]);
            free(first_items);
            free(files);
            return;
        }
    }
    while (len > 0)
    {
        size_t position = 0;
        //(key == '+') ? find min : find max
        int *MinOrMax = (int*)find_MinMax(first_items, len, sizeof(first_items[0]), cmp, &position);
        assert(NULL != MinOrMax);

        fprintf(f, "%d ", *MinOrMax);
        if (fscanf(files[position], "%d", &first_items[position]) != 1)
        {
            remove_from_arr(first_items, position, len, sizeof(first_items[0]));  //файл кончился - надо сдвинуть
            fclose(files[position]);
            remove_from_arr(files, position, len, sizeof(files[0]));
            len--;
        }
    }
    fclose(f);
    free(first_items);
    free(files);
}
