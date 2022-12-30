#ifndef HUFFMAN_HUFFMAN_H
#define HUFFMAN_HUFFMAN_H

#include <stddef.h>
#include <stdbool.h>

typedef struct h_node
{
	unsigned char ch;
	size_t freq;
	struct h_node * left;
	struct h_node * right;
} node_t;

typedef struct storage
{
	size_t capacity;
	size_t cur_size; //different symbols
	node_t ** arr1;
	node_t ** arr2;
} arrs_t;

node_t * create_node(unsigned char ch, size_t freq);

void free_tree(node_t * root);

bool is_leaf(node_t const * node);

#endif //HUFFMAN_HUFFMAN_H
