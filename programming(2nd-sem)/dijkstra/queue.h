#ifndef DIJKSTRA_QUEUE_H
#define DIJKSTRA_QUEUE_H

#include <stdbool.h>

typedef struct Q_node
{
	double weight;
	long long int id;
	struct Q_node * next;
} q_node;

typedef struct Queue
{
	q_node * head;
} queue_t;

queue_t * create_queue();

bool enqueue(queue_t * queue, double weight, long long int id);

long long int dequeue(queue_t * queue);

void free_queue(queue_t * q);

#endif //DIJKSTRA_QUEUE_H
