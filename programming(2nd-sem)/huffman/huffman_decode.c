#include "huffman_decode.h"
#include "huffman.h"
#include <assert.h>

#define BITS_IN_BYTE 8
#define CHECK_BIT(BYTE, IDX) ((BYTE)  & (1 << ((IDX) % BITS_IN_BYTE)))

static node_t * create_decoded_tree(FILE * input)
{
	assert(NULL != input);

	node_t * tree = create_node('R', 1);
	node_t * tmp = tree;
	if (NULL == tmp)
	{
		return NULL;
	}

	size_t symbols_num = 0; //number of symbols in meta
	fread(&symbols_num, sizeof(size_t), 1, input);
	if (ferror(input) || feof(input) || symbols_num <= 0)
	{
		perror("create tree ERROR : ");
		free_tree(tree);
		return NULL;
	}

	for (size_t i = 0; i < symbols_num; i++)
	{
		int ch = fgetc(input);
		if (ferror(input))
		{
			perror("create tree ERROR : ");
			free_tree(tree);
			return NULL;
		}

		int code_len = fgetc(input);
		if (ferror(input))
		{
			perror("create tree ERROR : ");
			free_tree(tree);
			return NULL;
		}

		for (int j = 0; j < code_len; j++)
		{
			int code_figure = fgetc(input);
			if ('0' == code_figure)
			{
				if (NULL == tmp->left)
				{
					node_t * interim_node = create_node('I', 1);
					if (NULL == interim_node)
					{
						free_tree(tree);
						return NULL;
					}
					tmp->left = interim_node;
				}
				tmp = tmp->left;
			}
			else if ('1' == code_figure)
			{
				if (NULL == tmp->right)
				{
					node_t * interim_node = create_node('I', 1);
					if (NULL == interim_node)
					{
						free_tree(tree);
						return NULL;
					}
					tmp->right = interim_node;
				}
				tmp = tmp->right;
			}
			else
			{
				perror("create tree ERROR : ");
				free_tree(tree);
				return NULL;
			}
		}
		tmp->ch = (unsigned char)ch;
		tmp = tree;
	}
	return tree;
}

static int decode_by_tree(FILE * encoded, FILE * decoded, node_t * tree, size_t decoded_len)
{
	assert(NULL != tree);
	assert(NULL != encoded);
	assert(NULL != decoded);
	assert(decoded_len > 0);

	int byte = 0;
	node_t * tmp = tree;
	while (EOF != (byte = fgetc(encoded)))
	{
		for (int i = 0; i < BITS_IN_BYTE; i++)
		{
			if (CHECK_BIT(byte, i))
			{
				tmp = tmp->right;
			}
			else
			{
				tmp = tmp->left;
			}

			if (is_leaf(tmp))
			{
				fputc((int)tmp->ch, decoded);
				if (ferror(decoded))
				{
					perror("decode_by_tree ERROR");
					return 1;
				}
				if (0 == (--decoded_len))
				{
					return 0;
				}
				tmp = tree;
			}
		}
	}
	return 1;
}

int decode(FILE * encoded, FILE * decoded, size_t decoded_len)
{
	assert(NULL != encoded);
	assert(NULL != decoded);

	assert(decoded_len > 0);

	node_t * tree = create_decoded_tree(encoded);
	if (NULL == tree)
	{
		return 1;
	}

	if (decode_by_tree(encoded, decoded, tree, decoded_len))
	{
		free_tree(tree);
		return 1;
	}

	free_tree(tree);
	return 0;
}