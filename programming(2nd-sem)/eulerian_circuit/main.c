#include <stdio.h>

#include "graph.h"

int main ()
{
	graph_t * graph = create_graph(NODES_COUNT);
	if (NULL == graph)
	{
		fprintf (stderr, "can't create graph\n");
		return 1;
	}
/*


	add_edge (1, 2, graph);
	add_edge (1, 10, graph);

	add_edge (2, 15, graph);
	add_edge (2, 14, graph);
	add_edge (2, 4, graph);
	add_edge (2, 3, graph);
	add_edge (2, 10, graph);

	add_edge (3, 4, graph);

	add_edge (4, 14, graph);
	add_edge (4, 13, graph);
	add_edge (4, 6, graph);
	add_edge (4, 5, graph);

	add_edge (5, 6, graph);

	add_edge (6, 7, graph);
	add_edge (6, 8, graph);
	add_edge (6, 12, graph);
	add_edge (6, 13, graph);

	add_edge (7, 8, graph);

	add_edge (8, 9, graph);
	add_edge (8, 11, graph);
	add_edge (8, 12, graph);
	add_edge (8, 10, graph);

	add_edge (9, 10, graph);

	add_edge (10, 11, graph);
	add_edge (10, 15, graph);

	add_edge (11, 12, graph);
	add_edge (11, 15, graph);

	add_edge (12, 13, graph);

	add_edge (13, 14, graph);

	add_edge (14, 15, graph);

 	add_edge (0, 1, graph);
	add_edge (0, 1, graph);
	add_edge (0, 5, graph);
	add_edge (0, 5, graph);
	add_edge (1, 2, graph);
	add_edge (1, 3, graph);
	add_edge (2, 3, graph);
	add_edge (3, 4, graph);
	add_edge (3, 5, graph);
	add_edge (4, 5, graph);

*/
	add_edge (0, 1, graph);
	add_edge (1, 2, graph);
	add_edge (2, 3, graph);
	add_edge (3, 4, graph);
	add_edge (4, 0, graph);
	add_edge (2, 5, graph);
	add_edge (5, 6, graph);
	add_edge (6, 7, graph);
	add_edge (7, 2, graph);

/*
	add_edge (0, 1, graph);
	add_edge (1, 2, graph);
	add_edge (2, 3, graph);
	add_edge (3, 4, graph);
	add_edge (4, 5, graph);
	add_edge (6, 7, graph);
	add_edge (7, 0, graph);
	add_edge (2, 8, graph);
	add_edge (2, 11, graph);
	add_edge (8, 9, graph);
	add_edge (9, 10, graph);
	add_edge (10, 5, graph);
	add_edge (5, 6, graph);
	add_edge (5, 11, graph);
	add_edge (4, 12, graph);
	add_edge (12, 13, graph);
	add_edge (13, 14, graph);
	add_edge (14, 4, graph);
	*/
	find_euler_circuit(graph, 0);
	free_graph(graph);

	return 0;
}
