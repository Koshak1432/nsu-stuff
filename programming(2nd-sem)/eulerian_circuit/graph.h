#ifndef EULERIAN_CIRCUIT_LIST_H
#define EULERIAN_CIRCUIT_LIST_H

#include <stdlib.h>
#include <stdbool.h>

#define NODES_COUNT 8

typedef struct List2
{
	size_t id;
	bool visited;
	struct List2 * next;
} list_t;

typedef struct Graph
{
	size_t nodes_count;
	list_t ** nodes;
	list_t * result;
	bool visited[NODES_COUNT];
} graph_t;

graph_t * create_graph(size_t nodes_count);

void add_edge(size_t id1, size_t id2, graph_t * graph);

void find_euler_circuit(graph_t * graph, size_t init_id);

void free_graph(graph_t * graph);

#endif//EULERIAN_CIRCUIT_LIST_H
