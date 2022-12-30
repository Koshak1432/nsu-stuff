#ifndef LINKED_LIST_H_INCLUDED
#define LINKED_LIST_H_INCLUDED
#include <stdio.h>

typedef struct linked_list
{
    int val;
    struct linked_list *next;
} list_t;

list_t * read_from_file(FILE * f, size_t *len);

void split_list(list_t *main_list, list_t ** left, list_t ** right, size_t mid);

void merge_sort(list_t **list, size_t len);

#endif // LINKED_LIST_H_INCLUDED
