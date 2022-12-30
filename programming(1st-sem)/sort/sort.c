#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <string.h>

static void swap(void *first, void *second, size_t size)
{
    assert(NULL != first);
    assert(NULL != second);
    void * tmp = malloc(size);
    memcpy(tmp, first, size);
    memcpy(first, second, size);
    memcpy(second, tmp, size);
    free(tmp);
}

static void partition(void *a, int start, int end, int *l, int *r, size_t elem_size,
               int (*cmp)(const void *, const void *))
{
    assert(NULL != a);
    char * a2 = (char *)a;
    if ((end - start) <= 1)
    {
        if (cmp(&a2[start * elem_size], &a2[end * elem_size]) > 0)
        {
            swap(&a2[start * elem_size], &a2[end * elem_size], elem_size);
        }
        *l = start;
        *r = end;
        return;
    }
    int left = start * elem_size;
    int right = end * elem_size;
    int head = start * elem_size;
    char *pivot = &a2[(start + end)/2 * elem_size];
    swap(pivot, &a2[right], elem_size);
    pivot = &a2[right];
    right -= elem_size;
    while (head <= right)
    {
        int compar = cmp(pivot, &a2[head]);
        if (compar > 0)
        {
            swap(&a2[head], &a2[left], elem_size);
            left += elem_size;
            head += elem_size;
        }
        else if (compar < 0)
        {
            swap(&a2[head], &a2[right], elem_size);
            right -= elem_size;
        }
        else
        {
            head += elem_size;
        }
    }
    // left stands on leftmost pivot
    // right stands on rightmost pivot
    // head stands on the next of right (first > pivot)
    swap(pivot, &a2[head], elem_size);
    *l = left/elem_size;
    *r = head / elem_size;
}

void sort(int *arr, int start, int end, int (*cmp)(const void *, const void *))
{
    assert(NULL != arr);
    if (start < end)
    {
        int left;
        int right;
        partition(arr, start, end, &left, &right, sizeof(arr[0]), cmp);
        sort(arr, start, left, cmp);
        sort(arr, right, end, cmp);
    }
}

