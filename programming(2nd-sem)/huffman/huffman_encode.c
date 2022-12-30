#include "huffman_encode.h"
#include "sort.h"
#include "codes_table.h"

#include <assert.h>
#include <stdlib.h>

#define ARR_SIZE 256
#define BITS_IN_BYTE 8
#define SET_BIT(BYTE, IDX) ((BYTE) |= (1 << ((IDX) % BITS_IN_BYTE)))
#define NULL_BIT(BYTE, IDX) ((BYTE) &= ~(1 << ((IDX) % BITS_IN_BYTE)))

void count_freq(FILE * f, size_t * freq_arr, size_t * len)
{
	assert(NULL != freq_arr);

	int ch = 0;
	while (EOF != (ch = fgetc(f)))
	{
		freq_arr[(unsigned int)ch]++;
		(*len)++;
	}
}

static node_t ** create_arr(size_t capacity)
{
	assert(capacity > 0);

	node_t ** arr1 = (node_t **)calloc(capacity, sizeof(node_t *));
	if (NULL == arr1)
	{
		return NULL;
	}

	for (size_t i = 0; i < capacity; i++)
	{
		arr1[i] = NULL;
	}
	return arr1;
}

arrs_t * create_arrs(size_t capacity)
{
	assert(capacity > 0);

	arrs_t * arrs = (arrs_t *)calloc(1, sizeof(*arrs));
	if (NULL == arrs)
	{
		return NULL;
	}

	arrs->capacity = capacity;
	arrs->cur_size = 0;
	arrs->arr1 = create_arr(capacity);
	if (NULL == arrs->arr1)
	{
		free(arrs);
		return NULL;
	}

	arrs->arr2 = create_arr(capacity);
	if (NULL == arrs->arr2)
	{
		free(arrs->arr1);
		free(arrs);
		return NULL;
	}
	return arrs;
}

static void free_arrs(arrs_t * arrs)
{
	assert(NULL != arrs);

	for (size_t i = 0; i < arrs->capacity; i++)
	{
		if (NULL != arrs->arr1[i])
		{
			free(arrs->arr1[i]);
		}
		if (NULL != arrs->arr2[i])
		{
			free(arrs->arr2[i]);
		}
	}
	free(arrs->arr1);
	free(arrs->arr2);
	free(arrs);
}

static int fill_arr(arrs_t * arrs, size_t const * freq)
{
	assert(NULL != arrs);
	assert(NULL != freq);

	for (size_t i = 0; i < arrs->capacity; i++)
	{
		if (freq[i] > 0)
		{
			arrs->arr1[arrs->cur_size++] = create_node((char)i, freq[i]);

			if (NULL == arrs->arr1[arrs->cur_size - 1])
			{
				free_arrs(arrs);
				return 1;
			}
		}
	}
	return 0;
}

static void arrs_sort(arrs_t * arrs)
{
	assert(NULL != arrs);

	sort(arrs->arr1, 0, arrs->cur_size - 1); // -1 because of: cur_size == 2 --> end == 2,
	// but there are only [0] and [1] idx in arr1
}

static node_t * find_min(arrs_t * arrs, size_t * idx1, size_t * idx2)
{
	assert(NULL != arrs);
	assert(*idx2 < arrs->capacity);

	node_t * first = NULL;
	if (*idx1 < arrs->capacity)
	{
		first = arrs->arr1[*idx1];
	}
	node_t * second = arrs->arr2[*idx2];

	if (NULL == second)
	{
		(*idx1)++;
		return first;
	}
	if (NULL == first)
	{
		(*idx2)++;
		return second;
	}
	if (first->freq > second->freq)
	{
		(*idx2)++;
		return second;
	}
	(*idx1)++;
	return first;
}

static int insert_to_arr(arrs_t * arrs, node_t * first, node_t * second, size_t * idx)
{
	assert(NULL != arrs);
	assert(NULL != first);
	assert(NULL != second);

	node_t * new_node = create_node('N', first->freq + second->freq); //todo ch == ??????????????????
	if (NULL == new_node)
	{
		return 1;
	}
	new_node->left = first;
	new_node->right = second;
	arrs->arr2[(*idx)++] = new_node;
	return 0;
}

static node_t * create_huffman_tree(arrs_t * arrs)
{
	assert(NULL != arrs);

	size_t idx1 = 0;
	size_t idx2 = 0;
	size_t insert_idx = 0;

	if (1 == arrs->cur_size)
	{
		return find_min(arrs, &idx1, &idx2);
	}
	while (idx2 < arrs->cur_size)
	{
		node_t * first = find_min(arrs, &idx1, &idx2);
		assert(NULL != first);
		node_t * second = find_min(arrs, &idx1, &idx2);

		if (NULL == second)
		{
			return first;
		}
		if (insert_to_arr(arrs, first, second, &insert_idx))
		{
			return NULL;
		}
	}
	return arrs->arr2[insert_idx - 1];
}

static int write_metadata(FILE * output, codes_table_t const * table)
{
	assert(NULL != table);

	fwrite(&table->symbols_num, sizeof(size_t), 1, output);
	if (ferror(output))
	{
		perror("writing_metadata ERROR :");
		return 1;
	}

	for (size_t i = 0; i < table->capacity; i++)
	{
		if (NULL != table->codes[i])
		{
			fputc((int)i, output);
			if (ferror(output))
			{
				perror("writing_metadata ERROR :");
				return 1;
			}
			fputc(table->codes[i]->code_len_in_bits, output);
			if (ferror(output))
			{
				perror("writing_metadata ERROR :");
				return 1;
			}
			fprintf(output, "%s", table->codes[i]->code);
			if (ferror(output))
			{
				perror("writing_metadata ERROR :");
				return 1;
			}
		}
	}
	return 0;
}

static void fill_rest_with_zeros(u_int8_t * byte, u_int8_t bits_filled, size_t bit_idx)
{
	while (0 != bits_filled % BITS_IN_BYTE)
	{
		NULL_BIT(*byte, bit_idx);
		bits_filled++;
		bit_idx++;
	}
}

static int write_main_data(FILE * input, FILE * output, codes_table_t const * table)
{
	assert(NULL != table);

	int ch = 0;
	size_t bit_idx = 0;
	u_int8_t bits_in_byte_filled = 0;
	u_int8_t byte = 0;
	code_t * code = NULL;

	while (EOF != (ch = fgetc(input)))
	{
		code = table->codes[(unsigned int)ch];
		for (int i = 0; i < code->code_len_in_bits; i++)
		{
			('0' == code->code[i]) ? NULL_BIT(byte, bit_idx) : SET_BIT(byte, bit_idx);
			bit_idx++;
			if (0 == bit_idx % BITS_IN_BYTE)
			{
				fputc(byte, output);
				if (ferror(output))
				{
					return 1;
				}
			}
			bits_in_byte_filled++;
		}
		bits_in_byte_filled %= BITS_IN_BYTE;
	}
	fill_rest_with_zeros(&byte, bits_in_byte_filled, bit_idx);
	fputc(byte, output);
	if (ferror(output))
	{
		return 1;
	}
	return 0;
}

int encode(FILE * input, FILE * output, size_t * len)
{
	assert(NULL != input);
	assert(NULL != output);

	size_t idx = 0;
	size_t freq[ARR_SIZE] = {0};
	char code[ARR_SIZE] = {0};

	arrs_t * arrs = create_arrs(ARR_SIZE);
	if (NULL == arrs)
	{
		return 1;
	}

	count_freq(input, freq, len);
	if (0 == *len)
	{
		free_arrs(arrs);
		return 0;
	}
	rewind(input);
	if (fill_arr(arrs, freq))
	{
		free_arrs(arrs);
		return 1;
	}

	arrs_sort(arrs);
	node_t * tree = create_huffman_tree(arrs);
	if (NULL == tree)
	{
		free_arrs(arrs);
		return 1;
	}
	codes_table_t * table = codes_table_create(ARR_SIZE);
	if (NULL == table)
	{
		free_arrs(arrs); //tree included
	}

	if (make_codes_table(table, tree, idx, code))
	{
		free_codes_table(table);
		free_arrs(arrs);
		return 1;
	}

	if (write_metadata(output, table))
	{
		free_codes_table(table);
		free_arrs(arrs);
		return 1;
	}
	if (write_main_data(input, output, table))
	{
		free_codes_table(table);
		free_arrs(arrs);
		return 1;
	}
	free_codes_table(table);
	free_arrs(arrs);
	return 0;
}