#ifndef TREE_H_INCLUDED
#define TREE_H_INCLUDED
#include "linked_list.h"

typedef struct tree
{
    int val;
    struct tree *left;
    struct tree *right;
} tree_t;

void print_tree(tree_t * root);

void free_tree(tree_t * root);

void make_tree_and_free_list(tree_t ** root, list_t * list, size_t len);

#endif // TREE_H_INCLUDED
