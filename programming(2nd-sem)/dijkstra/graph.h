#ifndef DIJKSTRA_GRAPH_H
#define DIJKSTRA_GRAPH_H

#include "hash_table.h"

double find_min_path(hash_tbl_t * table, long long int src_id, long long int dst_id, bool * error);

bool read_file(hash_tbl_t * table, FILE * input);

void print_result(hash_tbl_t const * table, double min_distance);

#endif //DIJKSTRA_GRAPH_H
