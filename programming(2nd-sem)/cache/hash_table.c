#include "hash_table.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

static list_t ** create_ptrs(hash_tbl_t const * table)
{
    assert(NULL != table);

    list_t ** ptrs = (list_t **)calloc(table->table_size, sizeof(list_t *));
    if (NULL == ptrs)
    {
        return NULL;
    }

    for (size_t i = 0; i < table->table_size; i++)
    {
        ptrs[i] = NULL;
    }
    return ptrs;
}

static void free_list(list_t * list)
{
    list_t *tmp = NULL;

    while (NULL != list)
    {
        tmp = list;
        list = list->next;
        free(tmp);
    }
}

static void free_ptrs(hash_tbl_t const * table)
{
    assert(NULL != table);

    for (size_t i = 0; i < table->table_size; i++)
    {
        if (NULL != table->ptrs[i])
        {
            free_list(table->ptrs[i]);
        }
    }
    free(table->ptrs);
}

hash_tbl_t * create_tbl(size_t size)
{
    assert(size > 0);

    hash_tbl_t * table = (hash_tbl_t *)calloc(1, sizeof(*table));
    if (NULL == table)
    {
        return NULL;
    }

    table->table_size = size;
    table->load = 0;
    table->ptrs = create_ptrs(table);
    if (NULL == table->ptrs)
    {
      return NULL;
    }
    return table;
}

void free_table(hash_tbl_t * table)
{
    assert(NULL != table);

    free_ptrs(table);
    free(table);
}

static size_t hash(long long int key, size_t len)
{
    assert(len > 0);
    if (key < 0)
    {
        key *= -1;
    }

    return key;
}

static list_t *create_element(list_t2 * cache_elem_ptr)
{
    assert(NULL != cache_elem_ptr);

    list_t *elem = (list_t *)malloc(sizeof(*elem));
    elem->ptr_to_cache = cache_elem_ptr;
    elem->next = NULL;
    return elem;
}

static list_t * add_elem(list_t * list, list_t2 * cache_elem_ptr) //to head
{
    assert(NULL != cache_elem_ptr);

    list_t *new_elem = create_element(cache_elem_ptr);
    new_elem->next = list;
    list = new_elem;
    return list;
}

void table_delete(hash_tbl_t const * table, long long int key)
{
    assert(NULL != table);

    size_t idx = hash(key, table->table_size);
    list_t * prev = table->ptrs[idx];

    if (NULL == prev)
    {
        return;
    }

    if (prev->ptr_to_cache->key == key)
    {
        free_list(prev);
        table->ptrs[idx] = NULL;
        return;
    }

    list_t * tmp = table->ptrs[idx]->next;

    while(NULL != tmp)
    {
        if (tmp->ptr_to_cache->key == key)
        {
            list_t *after_tmp = tmp->next;
            free(tmp);
            prev->next = after_tmp;
            return;
        }
        prev = prev->next;
        tmp = tmp->next;
    }
}
/*
void print_table(hash_tbl_t const *table)
{
    assert(NULL != table);

    for (size_t i = 0; i < table->table_size; i++)
    {
        printf("idx == %zu : ", i);
        list_t * elem = table->ptrs[i];
        while (NULL != elem)
        {
            printf("value of the cache_elem == %s, key == %lld",
                   elem->ptr->ptr_to_cache->val, elem->ptr->key);
            if (NULL != elem->next)
            {
                printf(" --> ");
            }
            elem = elem->next;
        }
        printf("\n");
    }
}
*/
static hash_tbl_t * table_rehash(hash_tbl_t * table)
{
    assert(NULL != table);

    hash_tbl_t * new_table = create_tbl(table->table_size * 2);
    if (NULL == new_table)
    {
        return NULL;
    }

    for (size_t i = 0; i < table->table_size; i++)
    {
        list_t * elem = table->ptrs[i];
        while (NULL != elem)
        {
            table_insert(&new_table, elem->ptr_to_cache);
            elem = elem->next;
        }
    }
    free_table(table);
    return new_table;
}

void table_insert(hash_tbl_t ** table, list_t2 * needed_ptr)
{
    assert(NULL != table);
    assert(NULL != *table);
    assert(NULL != needed_ptr);

    size_t idx = hash(needed_ptr->key, (*table)->table_size);
    list_t *tmp = (*table)->ptrs[idx];

    while (NULL != tmp)
    {
        if (needed_ptr->key == tmp->ptr_to_cache->key)
        {
            strcpy((*table)->ptrs[idx]->ptr_to_cache->val, needed_ptr->val);
            return;
        }
        tmp = tmp->next;
    }
    if ((*table)->load == (*table)->table_size)
    {
        *table = table_rehash(*table);
        if (NULL == *table)
        {
            fprintf(stderr, "can't create new table\n");
            return;
        }
        idx = hash(needed_ptr->key, (*table)->table_size);
    }
    (*table)->ptrs[idx] = add_elem((*table)->ptrs[idx], needed_ptr);
    (*table)->load++;
}

list_t2 * table_search(hash_tbl_t const * table, long long int key)
{
    assert(NULL != table);

    size_t idx = hash(key, table->table_size);
    list_t *elem = table->ptrs[idx];
    if (NULL == elem)
    {
        return NULL;
    }

    while(NULL != elem)
    {
        if (elem->ptr_to_cache->key == key)
        {
            return elem->ptr_to_cache;
        }
        elem = elem->next;
    }
    return NULL;
}