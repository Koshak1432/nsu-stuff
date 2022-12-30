#include "hash_table.h"

#include <assert.h>
#include <stdlib.h>

static list_t ** create_buckets(size_t size)
{
	list_t ** buckets = (list_t **)calloc(size, sizeof(list_t *));
	if (NULL == buckets)
	{
		return NULL;
	}

	for (size_t i = 0; i < size; i++)
	{
		buckets[i] = NULL;
	}
	return buckets;
}

static void free_list(list_t * list)
{
	assert(NULL != list);

	while (NULL != list)
	{
		while (NULL != list->ptr_to_node)
		{
			node_t * node_tmp = list->ptr_to_node;
			list->ptr_to_node = node_tmp->next;
			free(node_tmp);
		}
		list_t * tmp = list;
		list = list->next;
		free(tmp);
	}
}

static void free_buckets(list_t ** buckets, size_t size)
{
	for (size_t i = 0; i < size; i++)
	{
		if (NULL != buckets[i])
		{
			free_list(buckets[i]);
		}
	}
	free(buckets);
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
	table->result = NULL;
	table->buckets = create_buckets(size);
	if (NULL == table->buckets)
	{
		return NULL;
	}
	return table;
}

void free_result(list_t * result)
{
	while (NULL != result)
	{
		list_t * tmp = result;
		result = result->next;
		free(tmp);
	}
}

void free_table(hash_tbl_t * table)
{
	assert(NULL != table);

	free_buckets(table->buckets, table->table_size);
	free_result(table->result);
	free(table);
}

static size_t hash(long long int id, size_t size)
{
	assert(size > 0);

	if (id < 0)
	{
		id *= -1;
	}
	return id % size;
}

static list_t * create_element(node_t * node_ptr)
{
	assert(NULL != node_ptr);

	list_t * elem = (list_t *)calloc(1, sizeof(*elem));
	elem->ptr_to_node = node_ptr;
	elem->next = NULL;
	return elem;
}

void free_old_buckets(list_t ** buckets, size_t size) //without free node_t *
{
	assert(NULL != buckets);

	for (size_t i = 0; i < size; i++)
	{
		list_t * tmp = buckets[i];
		while (NULL != tmp)
		{
			buckets[i] = buckets[i]->next;
			free(tmp);
			tmp = buckets[i];
		}
	}
	free(buckets);
}

list_t * add_elem(list_t * list, node_t * node_ptr) //to head
{
	assert(NULL != node_ptr);

	list_t * new_elem = create_element(node_ptr);
	new_elem->next = list;
	list = new_elem;
	return list;
}

static list_t ** table_rehash(hash_tbl_t * table)
{
	assert(NULL != table);

	size_t old_size = table->table_size;
	list_t ** old_buckets = table->buckets;
	list_t ** new_buckets = create_buckets(table->table_size * 2);
	if (NULL == new_buckets)
	{
		free_old_buckets(old_buckets, old_size);
		return NULL;
	}

	table->buckets = new_buckets;
	table->table_size *= 2;
	table->load = 0;

	for (size_t i = 0; i < old_size; i++)
	{
		list_t * elem = old_buckets[i];
		while (NULL != elem)
		{
			table_insert(table, elem->ptr_to_node);
			elem = elem->next;
		}
	}

	free_old_buckets(old_buckets, old_size);
	return new_buckets;
}

int table_insert(hash_tbl_t * table, node_t * needed_ptr)
{
	assert(NULL != table);
	assert(NULL != needed_ptr);

	size_t idx = hash(needed_ptr->id, table->table_size);
	list_t * tmp = table->buckets[idx];

	while (NULL != tmp)
	{
		if (needed_ptr->id == tmp->ptr_to_node->id)
		{
			tmp->ptr_to_node = needed_ptr;
			return 0;
		}
		tmp = tmp->next;
	}
	if (table->load == table->table_size)
	{
		table->buckets = table_rehash(table);
		if (NULL == table->buckets)
		{
			return 1;
		}
		idx = hash(needed_ptr->id, table->table_size);
	}
	table->buckets[idx] = add_elem(table->buckets[idx], needed_ptr);
	table->load++;
	return 0;
}

node_t * table_search(hash_tbl_t const * table, long long int id)
{
	assert(NULL != table);

	size_t idx = hash(id, table->table_size);
	list_t * elem = table->buckets[idx];
	if (NULL == elem)
	{
		return NULL;
	}

	while (NULL != elem)
	{
		if (elem->ptr_to_node->id == id)
		{
			return elem->ptr_to_node;
		}
		elem = elem->next;
	}
	return NULL;
}
