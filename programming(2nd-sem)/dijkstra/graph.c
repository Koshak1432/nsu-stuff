#include "graph.h"

#include <assert.h>
#include <stdlib.h>
#include <math.h>

#include "queue.h"

static node_t * create_node(long long int id, double weight)
{
	node_t * node = (node_t *)calloc(1, sizeof(*node));
	if (NULL == node)
	{
		return NULL;
	}
	node->id = id;
	node->path_id = 0;
	node->weight = weight;
	node->next = NULL;
	return node;
}

static void add_neighbour(node_t * src_node, node_t * dst_node) //to head
{
	assert(NULL != src_node);
	assert(NULL != dst_node);

	node_t * tmp = src_node->next;
	dst_node->next = tmp;
	src_node->next = dst_node;
}

static bool isnot_in_table_add(hash_tbl_t * table, long long int id)
{
	assert(NULL != table);

	node_t * dst_in_table = table_search(table, id);
	if (NULL == dst_in_table)
	{
		dst_in_table = create_node(id, HUGE_VAL);
		if (NULL == dst_in_table)
		{
			return true;
		}
		if (table_insert(table, dst_in_table))
		{
			free(dst_in_table);
			return true;
		}
	}
	return false;
}

static bool add_node(hash_tbl_t * table, long long int src_id, long long int dst_id, double weight)
{
	assert(NULL != table);

	node_t * dst_node = create_node(dst_id, weight);
	if (NULL == dst_node)
	{
		return true;
	}
	node_t * src_node = table_search(table, src_id);
	if (NULL == src_node)
	{
		src_node = create_node(src_id, HUGE_VAL);
		if (NULL == src_node)
		{
			free(dst_node);
			return true;
		}
	}

	add_neighbour(src_node, dst_node);
	if (table_insert(table, src_node))
	{
		free(src_node);
		free(dst_node);
		return true;
	}

	if (isnot_in_table_add(table, dst_id))
	{
		return true;
	}
	return false;
}

static void make_path(hash_tbl_t * table, long long int src_id, long long int dst_id)
{
	assert(NULL != table);

	node_t * node = table_search(table, dst_id);
	assert(NULL != node);

	while (true)
	{
		table->result = add_elem(table->result, node);
		if (node->id == src_id)
		{
			return;
		}
		node = table_search(table, node->path_id);
	}
}

static void relaxation(hash_tbl_t * table, queue_t * queue, long long int current_id, long long int neighbour_id,
						double current_weight, double neighbour_weight)
{
	assert(NULL != table);

	node_t * neighbour_in_bucket = table_search(table, neighbour_id);
	assert(NULL != neighbour_in_bucket);

	if (true == neighbour_in_bucket->visited)
	{
		return;
	}

	double new_dist = neighbour_weight + current_weight;
	if (new_dist < neighbour_in_bucket->weight)
	{
		neighbour_in_bucket->weight = new_dist;
		neighbour_in_bucket->path_id = current_id;
		assert(!table_insert(table, neighbour_in_bucket));
		enqueue(queue, new_dist, neighbour_id);
	}
}

double find_min_path(hash_tbl_t * table, long long int src_id, long long int dst_id, bool * error)
{
	assert(NULL != table);
	assert(!(*error));

	queue_t * queue = create_queue(); //PQ (distance, id)
	if (NULL == queue)
	{
		*error = true;
		return 0;
	}

	node_t * current_node = table_search(table, src_id);
	if (NULL == current_node) //there is no node with id = src_id
	{
		free_queue(queue);
		*error = true;
		return 0;
	}
	current_node->weight = 0;
	assert(!table_insert(table, current_node));

	enqueue(queue, current_node->weight, src_id);
	while (NULL != queue->head)
	{
		long long int current_id = dequeue(queue);
		current_node = table_search(table, current_id);
		assert(NULL != current_node);

		current_node->visited = true;
		assert(!table_insert(table, current_node));
		node_t * neighbour = current_node->next;

		while (NULL != neighbour)
		{
			relaxation(table, queue, current_id, neighbour->id, current_node->weight, neighbour->weight);
			neighbour = neighbour->next;
		}

		if (current_id == dst_id)
		{
			make_path(table, src_id, dst_id);
			free_queue(queue);
			return current_node->weight;
		}
	}
	free_queue(queue);
	return HUGE_VAL;
}

bool read_file(hash_tbl_t * table, FILE * input)
{
	assert(NULL != table);
	assert(NULL != input);

	long long int src_id = 0;
	long long int dst_id = 0;
	double weight = 0;
	int ch = 0;

	while (EOF != (ch = fgetc(input)))
	{
		ungetc(ch, input);
		if (3 != fscanf(input, "%lld %lld %lf\n", &src_id, &dst_id, &weight))
		{
			return true;
		}
		add_node(table, src_id, dst_id, weight);
	}
	return false;
}

static void print_path(hash_tbl_t const * table)
{
	assert(NULL != table);

	list_t * tmp = table->result;
	while (NULL != tmp)
	{
		printf("%lld, ", tmp->ptr_to_node->id);
		tmp = tmp->next;
	}
	printf("\n");
}

void print_result(hash_tbl_t const * table, double min_distance)
{
	assert(NULL != table);

	printf("Min distance: %lf\n", min_distance);
	print_path(table);
}
