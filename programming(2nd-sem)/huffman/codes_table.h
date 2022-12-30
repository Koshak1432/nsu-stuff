#ifndef HUFFMAN_CODES_TABLE_H
#define HUFFMAN_CODES_TABLE_H

#include "huffman.h"

#define ARR_SIZE 256

typedef struct Code
{
	int code_len_in_bits;
	char code[ARR_SIZE];
} code_t;

typedef struct Codes_table
{
	size_t capacity;
	size_t symbols_num;
	code_t ** codes;
} codes_table_t;

codes_table_t * codes_table_create(size_t capacity);
int make_codes_table(codes_table_t * table, node_t * root, size_t idx, char * code);
void free_codes_table(codes_table_t * table);

#endif //HUFFMAN_CODES_TABLE_H
