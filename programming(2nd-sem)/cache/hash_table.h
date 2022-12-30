#ifndef HASH_TABLE_H_INCLUDED
#define HASH_TABLE_H_INCLUDED

#include "list.h"

#include <stdint.h>

typedef struct linked_list
{
    list_t2 * ptr_to_cache;
    struct linked_list *next;
} list_t;

typedef struct
{
    size_t table_size;
    size_t load;
    list_t **ptrs;
} hash_tbl_t;

hash_tbl_t * create_tbl(size_t size);

void free_table(hash_tbl_t * table);

void table_delete(hash_tbl_t const * table, long long int key);

void table_insert(hash_tbl_t ** table, list_t2 * needed_ptr);

list_t2 * table_search(hash_tbl_t const * table, long long int key);

#endif // HASH_TABLE_H_INCLUDED
