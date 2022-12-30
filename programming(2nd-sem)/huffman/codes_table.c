#include "codes_table.h"

#include <assert.h>
#include <string.h>
#include <stdlib.h>

static code_t * code_create(int code_len, char const * code)
{
	assert(NULL != code);
	assert(code_len > 0);

	code_t * new_code = (code_t *)calloc(1, sizeof(*new_code));
	if (NULL == new_code)
	{
		return NULL;
	}

	memcpy(new_code->code, code, code_len);
	new_code->code_len_in_bits = code_len;
	return new_code;
}

static code_t ** codes_create(size_t capacity)
{
	assert(capacity > 0);

	code_t ** codes = (code_t **)calloc(capacity, sizeof(code_t *));
	if (NULL == codes)
	{
		return NULL;
	}

	for (size_t i = 0; i < capacity; i++)
	{
		codes[i] = NULL;
	}
	return codes;
}

codes_table_t * codes_table_create(size_t capacity)
{
	assert(capacity > 0);

	codes_table_t * table = (codes_table_t *)calloc(1, sizeof(*table));
	if (NULL == table)
	{
		return NULL;
	}
	table->capacity = capacity;
	table->symbols_num = 0;
	table->codes = codes_create(capacity);
	if (NULL == table->codes)
	{
		free(table);
		return NULL;
	}
	return table;
}

static int code_add(codes_table_t * table, unsigned char ch, int code_len, char const * code)
{
	assert(NULL != table);
	assert(NULL != code);
	assert(code_len > 0);

	code_t * new_code = code_create(code_len, code);
	if (NULL == new_code)
	{
		return 1;
	}
	table->codes[(unsigned int)ch] = new_code;
	table->symbols_num++;
	return 0;
}

int make_codes_table(codes_table_t * table, node_t * root, size_t idx, char * code)
{
	assert(NULL != table);
	assert(NULL != root);
	assert(NULL != code);

	if (NULL != root->left)
	{
		code[idx] = '0';
		if (make_codes_table(table, root->left, idx + 1, code))
		{
			return 1;
		}
	}
	if (NULL != root->right)
	{
		code[idx] = '1';
		if (make_codes_table(table, root->right, idx + 1, code))
		{
			return 1;
		}
	}

	if (is_leaf(root))
	{
		if (0 == idx)
		{
			code[idx] = '1';
		}
		if (code_add(table, root->ch, (int)strlen(code), code))
		{
			return 1;
		}
		if (idx > 0)
		{
			code[idx - 1] = '\0';
		}
	}
	return 0;
}

static void free_codes(codes_table_t * table)
{
	assert(NULL != table);
	assert(NULL != table->codes);

	for (size_t i = 0; i < table->capacity; i++)
	{
		if (NULL != table->codes[i])
		{
			free(table->codes[i]);
		}
	}
	free(table->codes);
}

void free_codes_table(codes_table_t * table)
{
	assert(NULL != table);

	free_codes(table);
	free(table);
}