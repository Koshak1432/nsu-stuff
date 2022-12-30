#include "cache.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>

#define CAPACITY 5

cache_t * create_cache(size_t capacity)
{
  cache_t * cache = (cache_t *)calloc(1, sizeof(*cache));
  if (NULL == cache)
  {
    return NULL;
  }
  cache->capacity = 0;
  cache->table = create_tbl(capacity * 3);
  cache->tail = NULL;
  cache->head = NULL;
  return cache;
}

static void move_to_head(list_t2 ** head, list_t2 * elem, list_t2 ** tail)
{
  assert(NULL != elem);

  if (NULL == elem->prev || NULL == elem)
  {
    return;
  }
  elem->prev->next = elem->next;
  if (NULL != elem->next)
  {
    elem->next->prev = elem->prev;
  }
  else
  {
    *tail = (*tail)->prev;
  }

  (*head)->prev = elem;
  elem->next = *head;
  elem->prev = NULL;
  *head = elem;
}

void cache_put(cache_t * cache, long long int key, char const * value)
{
  assert(NULL != cache);

  list_t2 * table_elem = table_search(cache->table, key);
  if (NULL != table_elem)
  {
    strcpy(table_elem->val, value);
    move_to_head(&cache->head, table_elem, &cache->tail);
    return;
  }
  if (cache->capacity >= CAPACITY)
  {
    table_delete(cache->table, cache->tail->key);
    delete_elem(&cache->tail);
  }

  add_elem2(&cache->head, value, key);
  table_insert(&cache->table, cache->head);

  if (0 == cache->capacity)
  {
    cache->tail = cache->head; //docking
  }
  cache->capacity++;
}

char * cache_get(cache_t * cache, long long int key)
{
  assert(NULL != cache);

  list_t2 * table_elem = table_search(cache->table, key);
  if (NULL == table_elem)
  {
    printf("there is no elem with key %lli\n", key);
    return NULL;
  }
  move_to_head(&cache->head, table_elem, &cache->tail);
  return table_elem->val;
}

void cache_print(cache_t const * cache)
{
  assert(NULL != cache);

  list_t2 * tmp = cache->head;

  while (tmp != NULL)
  {
    printf("%s->", tmp->val);
    tmp = tmp->next;
  }
  printf("\n");
}

void free_cache(cache_t * cache)
{
  assert(NULL != cache);

  free_table(cache->table);
  free_list2(cache->head);
}