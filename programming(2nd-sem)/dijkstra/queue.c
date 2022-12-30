#include "queue.h"

#include <assert.h>
#include <stdlib.h>

queue_t * create_queue()
{
	queue_t * queue = (queue_t *)calloc(1, sizeof (*queue));
	if (NULL == queue)
	{
		return NULL;
	}
	queue->head = NULL;
	return queue;
}

static q_node * create_node(double weight, long long int id)
{
	q_node * node = (q_node *)calloc(1, sizeof (*node));
	if (NULL == node)
	{
		return NULL;
	}
	node->next = NULL;
	node->weight = weight;
	node->id = id;

	return node;
}

bool enqueue(queue_t * queue, double weight, long long int id)
{
	q_node * new_node = create_node(weight, id);
	if (NULL == new_node)
	{
		return true;
	}

	q_node * current = queue->head; // 1 | NULL
	if (NULL == current)
	{
		queue->head = new_node;
		return false;
	}

	if (weight <= current->weight) // 1 | 3 ->
	{
		new_node->next = current;
		queue->head = new_node;
		return false;
	}

	while (NULL != current->next)  // 1 | 1 -> 5
	{
		if (current->weight <= weight && current->next->weight > weight)
		{
			new_node->next = current->next;
			current->next = new_node;
			return false;
		}
		current = current->next;
	}
	current->next = new_node;
	return false;
}

long long int dequeue(queue_t * queue)
{
	assert(NULL != queue);

	q_node * tmp = queue->head;
	if (NULL == tmp) //empty
	{
		return 0;
	}

	long long int id = tmp->id;
	queue->head = tmp->next;

	free(tmp);
	return id;
}

void free_queue(queue_t * queue)
{
	assert(NULL != queue);

	while (NULL != queue->head)
	{
		q_node * tmp = queue->head;
		queue->head = tmp->next;
		free(tmp);
	}
	free(queue);
}
