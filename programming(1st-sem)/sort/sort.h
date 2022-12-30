#ifndef SORT_H_INCLUDED
#define SORT_H_INCLUDED

#include <stddef.h>

void sort(int *arr, int start, int end, int (*cmp)(const void *, const void *));

void partition(void *a, int start, int end, int *l, int *r,
               size_t elem_size, int (*cmp)(const void *, const void *));

#endif // SORT_H_INCLUDED
