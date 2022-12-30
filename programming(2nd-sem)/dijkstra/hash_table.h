#ifndef DIJKSTRA_HASH_TABLE_H
#define DIJKSTRA_HASH_TABLE_H

#include <stdio.h>
#include <stdbool.h>

typedef struct Node
{
	double weight;
	long long int id;
	long long int path_id;
	bool visited;
	struct Node * next;
} node_t;

typedef struct linked_list
{
	node_t * ptr_to_node;
	struct linked_list * next;
} list_t;

typedef struct
{
	size_t table_size;
	size_t load;
	list_t ** buckets;
	list_t * result;
} hash_tbl_t;

list_t * add_elem(list_t * list, node_t * node_ptr);

hash_tbl_t * create_tbl(size_t size);

void free_table(hash_tbl_t * table);

int table_insert(hash_tbl_t * table, node_t * needed_ptr);

node_t * table_search(hash_tbl_t const * table, long long int id);

#endif //DIJKSTRA_HASH_TABLE_H
