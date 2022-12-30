#include "tree.h"
#include <stdlib.h>

void print_tree(tree_t * root) //infix
{
    if (NULL == root)
    {
        return;
    }
    printf("(");
    print_tree(root->left);
    printf("%d", root->val);
    print_tree(root->right);
    printf(")");
}

void free_tree(tree_t * root)
{
    if(NULL == root)
    {
        return;
    }
    free_tree(root->left);
    free_tree(root->right);
    free(root);
}

static tree_t * create_tree_node(int value)
{
    tree_t * node = (tree_t *)malloc(sizeof(*node));
    node->val = value;
    node->left = NULL;
    node->right = NULL;
    return node;
}

void make_tree_and_free_list(tree_t ** root, list_t * list, size_t len)
{
    if (NULL == list)
    {
        *root = NULL; //prog works right without it, but with it it looks more logical
        return;
    }
    if (len == 1)
    {
        *root = create_tree_node(list->val);
        free(list);
        return;
    }
    list_t * left = NULL;
    list_t * right = NULL;
    size_t mid = len / 2;

    split_list(list, &left, &right, mid);

    *root = create_tree_node(right->val);
    list_t *right_next = right->next; // чтобы фришнуть ноду из списка
    free(right);
    make_tree_and_free_list(&(*root)->left, left, mid);
    make_tree_and_free_list(&(*root)->right, right_next, len - mid - 1);
}
