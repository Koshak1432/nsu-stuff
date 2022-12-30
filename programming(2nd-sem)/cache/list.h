#ifndef LIST_H_INCLUDED
#define LIST_H_INCLUDED

#include <stdbool.h>

#define VALUE_LEN 100

typedef struct linked_list2
{
    char val[VALUE_LEN];
    long long int key;
    struct linked_list2 *next;
    struct linked_list2 *prev;
} list_t2;

void add_elem2(list_t2 ** list, char const * value, long long key);

void delete_elem(list_t2 ** tail);

void free_list2(list_t2 *list);

#endif // LIST_H_INCLUDED
