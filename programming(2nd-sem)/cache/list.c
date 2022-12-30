#include "list.h"
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

static list_t2 * create_element2(char const * value, long long key)
{
    list_t2 *element = (list_t2 *)calloc(1, sizeof(*element));
    if (NULL == element)
    {
        fprintf(stderr, "can't allocate memory for the list_t2 element\n");
        return NULL;
    }
    strcpy(element->val, value);
    element->key = key;
    element->next = NULL;
    element->prev = NULL;
    return element;
}

void free_list2(list_t2 *list)
{
    list_t2 * tmp = NULL;
    while (NULL != list)
    {
        tmp = list;
        list = list->next;
        free(tmp);
    }
}

void add_elem2(list_t2 ** list, char const * value, long long int key)
{
    assert(NULL != list);

    list_t2 *new_elem = create_element2(value, key);

    if (NULL != *list)
    {
        (*list)->prev = new_elem;
        new_elem->next = *list;
        *list = new_elem;
        return;
    }
    *list = new_elem;
}

void delete_elem(list_t2 ** tail)
{
    assert(NULL != tail);
    assert(NULL != *tail);

    list_t2 * tmp = *tail;
    *tail = tmp->prev;
    (*tail)->next = NULL;

    free(tmp);
}
