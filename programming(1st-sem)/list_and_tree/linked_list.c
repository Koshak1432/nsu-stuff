#include <stdio.h>
#include <assert.h>
#include <stdlib.h>
#include "linked_list.h"


static list_t *create_node(int value)
{
    list_t *node = (list_t *)malloc(sizeof(*node));
    node->val = value;
    node->next = NULL;
    return node;
}

/*void print_list(list_t *list) //debug ficha
{
    while (list != NULL)
    {
        printf("%d-> ", list->val);
        list = list->next;
    }
    printf("\n");
}

void free_list(list_t *list)
{
    list_t *tmp;
    while (NULL != list)
    {
        tmp = list;
        list = list->next;
        printf("freeshnul nodu with value == %d\n", tmp->val);
        free(tmp);
    }
}
*/
list_t * read_from_file(FILE * f, size_t *len)
{
    list_t *head = NULL;
    list_t ** end = &head; //место,куда надо вставить ноду
    int x = 0;
    while (fscanf(f, "%d", &x) == 1)
    {
        *end = create_node(x);
        end = &(*end)->next;
        (*len)++;
    }
    return head;
}

static list_t *merge_lists(list_t * left, list_t * right)
{
    if (NULL == left)
    {
        return right;
    }
    if (NULL == right)
    {
        return left;
    }

    list_t * head = NULL;
    if (left->val <= right->val)
    {
        head = left;
    }
    else
    {
        head = right;
    }
    list_t ** end = &head;

    while (NULL != left && NULL != right)
    {
        if (left->val <= right->val)
        {
            *end = left;
            left = left->next;
            end = &(*end)->next;
        }
        else
        {
            *end = right;
            right = right->next;
            end = &(*end)->next;
        }
    }
    if (NULL != left)
    {
        *end = left;
    }
    else
    {
        *end = right;
    }
    return head;
}

void split_list(list_t *main_list, list_t ** left, list_t ** right, size_t mid)
{
    list_t *tmp = main_list;
    for (size_t i = 1; i < mid; i++)
    {
        tmp = tmp->next;
    }
    *right = tmp->next;
    tmp->next = NULL;
    *left = main_list;
}

void merge_sort(list_t **list, size_t len)
{
    assert(NULL != list);
    if (len < 2)
    {
        return;
    }

    list_t * left = NULL;
    list_t *right = NULL;

    size_t mid = len / 2;
    split_list(*list, &left, &right, mid);

    merge_sort(&left, mid);
    merge_sort(&right, len - mid);

    *list = merge_lists(left, right);
}
