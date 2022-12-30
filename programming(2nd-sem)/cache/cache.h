#ifndef CACHE_CACHE_H
#define CACHE_CACHE_H

#include "hash_table.h"

typedef struct Cache
{
  hash_tbl_t * table;
  list_t2 * head;
  list_t2 * tail;
  size_t capacity;
} cache_t;

cache_t * create_cache(size_t capacity);

void cache_put(cache_t * cache, long long int key, char const * value);

char * cache_get(cache_t * cache, long long int key);

void cache_print(cache_t const * cache);

void free_cache(cache_t * cache);

#endif // CACHE_CACHE_H
