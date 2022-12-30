#include "huffman.h"

#include <assert.h>
#include <stdlib.h>

node_t * create_node(unsigned char ch, size_t freq)
{
	node_t * node = (node_t *)malloc(sizeof(*node));
	if (NULL == node)
	{
		return NULL;
	}
	node->ch = ch;
	node->freq = freq;
	node->left = NULL;
	node->right = NULL;
	return node;
}

void free_tree(node_t * root)
{
	if (NULL == root)
	{
		return;
	}
	free_tree(root->left);
	free_tree(root->right);
	free(root);
}

bool is_leaf(node_t const * node)
{
	assert(NULL != node);

	return NULL == node->left && NULL == node->right;
}


