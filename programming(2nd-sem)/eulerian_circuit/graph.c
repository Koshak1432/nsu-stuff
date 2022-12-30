#include "graph.h"

#include <stdio.h>
#include <assert.h>

static list_t * create_node(size_t id)
{
	assert (id >= 0);
	assert(id < NODES_COUNT);

	list_t * node = (list_t *)calloc(1, sizeof (* node));
	if (NULL == node)
	{
		return NULL;
	}
	node->id = id;
	node->visited = false;
	node->next = NULL;
	return node;
}

static list_t ** create_nodes(graph_t const * graph)
{
	list_t ** nodes = (list_t **)calloc (graph->nodes_count, sizeof(list_t *));
	if (NULL == nodes)
	{
		return NULL;
	}

	for (size_t i = 0; i < graph->nodes_count; i++)
	{
		nodes[i] = NULL;
	}
	return nodes;
}

graph_t * create_graph(size_t nodes_count)
{
	assert (nodes_count > 0);

	graph_t * graph = (graph_t *)calloc(1, sizeof (* graph));
	if (NULL == graph)
	{
		return NULL;
	}
	graph->nodes_count = nodes_count;
	for (size_t i = 0; i < nodes_count; i++)
	{
		graph->visited[i] = false;
	}
	graph->result = NULL;
	graph->nodes = create_nodes (graph);
	if (NULL == graph->nodes)
	{
		free(graph);
		return NULL;
	}
	return graph;
}

static void add_node(size_t src_id, size_t dst_id, graph_t * graph)
{
	assert (NULL != graph);
	assert(src_id < NODES_COUNT);
	assert(dst_id < NODES_COUNT);

	if (NULL == graph->nodes[src_id])
	{
		list_t * src = create_node(src_id);
		if (NULL == src)
		{
			fprintf (stderr, "CAN'T CREATE SRC NODE\n");
			return;
		}
		graph->nodes[src_id] = src;
	}
	list_t * dst = create_node (dst_id);
	if (NULL == dst)
	{
		fprintf (stderr, "CAN'T CREATE DST NODE\n");
		return;
	}

	list_t * tmp = graph->nodes[src_id];
	while (NULL != tmp->next)
	{
		tmp = tmp->next;
	}
	tmp->next = dst;
}

void add_edge(size_t id1, size_t id2, graph_t * graph)
{
	assert (NULL != graph);
	assert(id1 < NODES_COUNT);
	assert(id2 < NODES_COUNT);

	add_node (id1, id2, graph);
	add_node (id2, id1, graph);
}

static bool is_even_degree(graph_t const * graph)
{
	assert (NULL != graph);

	for (size_t i = 0; i < graph->nodes_count; i++)
	{
		size_t node_count = 0;
		list_t * tmp = graph->nodes[i];
		if (NULL == tmp) //is it possible??
		{
			continue;
		}

		while (NULL != tmp->next)
		{
			node_count++;
			tmp = tmp->next;
		}
		if (node_count % 2 != 0)
		{
			return false;
		}
	}
	return true;
}

static bool is_connected(graph_t const * graph, size_t init)
{
	assert(NULL != graph);

	for (size_t i = init; i < graph->nodes_count; i++)
	{
		if (!graph->visited[i])
		{
			return false;
		}
	}
	return true;
}

static void add_to_result(list_t ** head, size_t id)
{
	assert (NULL != head);
	assert(id < NODES_COUNT);

	list_t * new_node = create_node(id);
	if (NULL == new_node)
	{
		fprintf(stderr, "can't create node with id %zu to result\n", id);
		return;
	}
	new_node->next = * head;
	if (NULL != head)
	{
		* head = new_node;
	}
}

static void print_result(list_t * result)
{
	assert(NULL != result);

	list_t * tmp = result;
	while (NULL != tmp)
	{
		printf("%zu   ", tmp->id);
		tmp=tmp->next;
	}
}

static void make_visited(graph_t const * graph, size_t src_id, size_t dst_id)
//src_id -- from where, dst_id -- what
{
	assert(NULL != graph);
	assert(src_id < NODES_COUNT);
	assert(dst_id < NODES_COUNT);

	list_t * tmp_back = graph->nodes[src_id];
	if (NULL == tmp_back)
	{
		return;
	}

	list_t * tmp_forward = tmp_back->next;
	while (NULL != tmp_forward)
	{
		if (tmp_forward->id == dst_id && !tmp_forward->visited)
		{
			tmp_forward->visited = true;
			return;
		}
		tmp_back = tmp_back->next;
		tmp_forward = tmp_forward->next;
	}
}

static void dfs(graph_t * graph, size_t id)
{
	assert(NULL != graph);
	assert(id < NODES_COUNT);

	graph->visited[id] = true;
	list_t * tmp = graph->nodes[id]->next;

	while(NULL != tmp)
	{
		if (!tmp->visited)
		{
			size_t dst_id = tmp->id;
			tmp->visited = true;
			make_visited(graph, dst_id, id);
			dfs(graph, dst_id);
		}
		tmp = tmp->next;
	}

	add_to_result(&graph->result, id);
}

void find_euler_circuit(graph_t * graph, size_t init_id)
{
	assert(NULL != graph);
	assert(init_id >= 0);

	if (is_even_degree(graph))
	{
		dfs(graph, init_id); //do not forget about init!!!

		if (!is_connected(graph, init_id))
		{
			printf("graph isn't connected -- there is no euler circuit\n");
			return;
		}
		print_result(graph->result);
	}
	else
	{
		printf("graph has at least one odd degree -- there is no euler circuit\n");
	}
}

static void free_list(list_t * list)
{
	list_t * tmp = NULL;
	while (NULL != list)
	{
		tmp = list;
		list = list->next;
		free(tmp);
	}
}

static void free_nodes(graph_t * graph)
{
	assert(NULL != graph);

	for (size_t i = 0; i < graph->nodes_count; i++)
	{
		if (NULL != graph->nodes[i])
		{
			free_list(graph->nodes[i]);
		}
	}
	free(graph->nodes);
}

void free_graph(graph_t * graph)
{
	assert(NULL != graph);

	free_nodes(graph);
	free_list(graph->result);
	free(graph);
}