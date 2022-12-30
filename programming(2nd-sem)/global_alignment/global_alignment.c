#include "global_alignment.h"
#include <assert.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <math.h>

enum Directions
{
	LEFT,
	TOP,
	DIAG,
};

enum errors
{
	OK,
	FIRST_INPUT_ERROR,
	SECOND_INPUT_ERROR,
	CREATING_MATRIX_ERROR,
};

typedef struct Cell
{
	size_t left_gaps;
	size_t top_gaps;
	enum Directions from_where;
	double diag_score;
	double left_score;
	double top_score;
} cell_t;

typedef struct Matrix
{
	size_t height;
	size_t width;
	char * first_input;
	char * second_input;
	char * first_seq;
	char * second_seq;
	char * inter_str;
	cell_t ** matrix;
} score_matrix_t;

#define GAP_OPEN 10
#define GAP_EXTEND 0.5
#define BUFFER_SIZE 5
#define SYMBOLS_PER_LINE 50

#define MAX3(A, B, C) (((A) > (B)) ? (((A) > (C)) ? (A) : (C)) : (((B) > (C))) ? (B) : (C))
#define MAX2(A, B) (((A) > (B)) ? (A) : (B))

static const double SUBSTITUTION[4][4] = {{5,  -4, -4, -4},
										  {-4, 5,  -4, -4},
										  {-4, -4, 5,  -4},
										  {-4, -4, -4, 5}};

static size_t get_sub_idx(char symbol)
{
	if ('A' == symbol)
	{
		return 0;
	}
	if ('T' == symbol)
	{
		return 1;
	}
	if ('G' == symbol)
	{
		return 2;
	}
	if ('C' == symbol)
	{
		return 3;
	}
	return (size_t)-1;
}

static int create_sequences(score_matrix_t * matrix)
{
	assert(NULL != matrix);

	size_t len = strlen(matrix->first_input) + strlen(matrix->second_input) + 1;

	matrix->first_seq = (char *)calloc(len, sizeof(char));
	if (NULL == matrix->first_seq)
	{
		return 1;
	}
	matrix->second_seq = (char *)calloc(len, sizeof(char));
	if (NULL == matrix->second_seq)
	{
		free(matrix->first_seq);
		return 1;
	}
	matrix->inter_str = (char *)calloc(len, sizeof(char));
	if (NULL == matrix->inter_str)
	{
		free(matrix->first_seq);
		free(matrix->second_seq);
		return 1;
	}
	matrix->first_seq[len - 1] = '\0';
	matrix->second_seq[len - 1] = '\0';
	matrix->inter_str[len - 1] = '\0';
	return 0;
}

static cell_t ** create_matrix(size_t height, size_t width)
{
	assert(width > 0);
	assert(height > 0);

	cell_t ** matrix = (cell_t **)calloc(height, sizeof(cell_t *));
	if (NULL == matrix)
	{
		return NULL;
	}

	for (size_t i = 0; i < height; i++)
	{
		matrix[i] = (cell_t *)calloc(width, sizeof(*matrix[i]));
		if (NULL == matrix[i])
		{
			for (size_t j = i; j >= 0; j--)
			{
				free(matrix[j]);
			}
			free(matrix);
			return NULL;
		}
	}
	return matrix;
}

static void free_interim_matrix(score_matrix_t * matrix)
{
	assert(NULL != matrix);

	for (size_t i = 0; i < matrix->height; i++)
	{
		free(matrix->matrix[i]);
	}
	free(matrix->matrix);
}

static void free_scoring_matrix(score_matrix_t * matrix)
{
	assert(NULL != matrix);

	if (NULL != matrix->first_seq)
	{
		free(matrix->first_seq);
	}
	if (NULL != matrix->second_seq)
	{
		free(matrix->second_seq);
	}
	if (NULL != matrix->inter_str)
	{
		free(matrix->inter_str);
	}
	if (NULL != matrix->first_input)
	{
		free(matrix->first_input);
	}
	if (NULL != matrix->second_input)
	{
		free((matrix->second_input));
	}

	free_interim_matrix(matrix);
	free(matrix);
}

static score_matrix_t * create_score_matrix(char * first_input, char * second_input)
{
	assert(NULL != first_input);
	assert(NULL != second_input);

	score_matrix_t * score_matrix = (score_matrix_t *)calloc(1, sizeof(score_matrix_t));
	if (NULL == score_matrix)
	{
		return NULL;
	}
	size_t height = strlen(second_input) + 1;
	size_t width = strlen(first_input) + 1;

	score_matrix->matrix = create_matrix(height, width);
	if (NULL == score_matrix->matrix)
	{
		free(score_matrix);
		return NULL;
	}
	score_matrix->first_input = first_input;
	score_matrix->second_input = second_input;
	score_matrix->width = width;
	score_matrix->height = height;

	if (create_sequences(score_matrix))
	{
		free_interim_matrix(score_matrix);
		free(score_matrix);
		return NULL;
	}
	return score_matrix;
}

static void first_init(score_matrix_t * matrix)
{
	assert(NULL != matrix);

	cell_t begin = matrix->matrix[0][0];
	begin.from_where = DIAG;
	begin.top_score = 0;
	begin.left_score = 0;
	begin.diag_score = 0;
	begin.left_gaps = 0;
	begin.top_gaps = 0;
	matrix->matrix[0][0] = begin;

	for (size_t column = 1; column < matrix->width; column++)
	{
		cell_t current = matrix->matrix[0][column];
		current.from_where = LEFT;
		current.left_score -= GAP_OPEN + GAP_EXTEND * (double)(column - 1);
		current.top_score = -HUGE_VAL;
		current.diag_score = -HUGE_VAL;
		current.left_gaps = matrix->matrix[0][column - 1].left_gaps + 1;
		current.top_gaps = 0;
		matrix->matrix[0][column] = current;
	}

	for (size_t row = 1; row < matrix->height; row++)
	{
		cell_t current = matrix->matrix[row][0];
		current.from_where = TOP;
		current.top_score -= GAP_OPEN + GAP_EXTEND * (double)(row - 1);
		current.left_score = -HUGE_VAL;
		current.diag_score = -HUGE_VAL;
		current.top_gaps = matrix->matrix[row - 1][0].top_gaps + 1;
		current.left_gaps = 0;
		matrix->matrix[row][0] = current;
	}
}

static void count_cell(score_matrix_t * matrix, size_t row, size_t column)
{
	assert(NULL != matrix);
	assert(row > 0);
	assert(column > 0);

	size_t sub_idx1 = get_sub_idx(matrix->first_input[column - 1]);
	size_t sub_idx2 = get_sub_idx(matrix->second_input[row - 1]);
	assert(sub_idx2 < 4);
	assert(sub_idx2 < 4);
	double matching_cost = SUBSTITUTION[sub_idx1][sub_idx2];

	cell_t current = matrix->matrix[row][column];
	cell_t left = matrix->matrix[row][column - 1];
	cell_t top = matrix->matrix[row - 1][column];
	cell_t diag = matrix->matrix[row - 1][column - 1];

	current.diag_score = MAX3(diag.diag_score + matching_cost,
							  diag.left_score + matching_cost, diag.top_score + matching_cost);

	current.left_score = MAX2(left.diag_score - GAP_OPEN, left.top_score - GAP_OPEN);
	if (left.left_score - GAP_EXTEND > current.left_score && left.left_gaps > 0)
	{
		current.left_score = left.left_score - GAP_EXTEND;
		current.left_gaps = left.left_gaps + 1;
	}
	else
	{
		current.left_gaps = 1;
	}

	current.top_score = MAX2(top.diag_score - GAP_OPEN, top.left_score - GAP_OPEN);
	if (top.top_score - GAP_EXTEND > current.top_score && top.top_gaps > 0)
	{
		current.top_score = top.top_score - GAP_EXTEND;
		current.top_gaps = top.top_gaps + 1;
	}
	else
	{
		current.top_gaps = 1;
	}

	double cell_max = MAX3(current.diag_score, current.left_score, current.top_score);

	if (cell_max == current.diag_score)
	{
		current.from_where = DIAG;
	}
	else
	{
		(cell_max == current.left_score) ? (current.from_where = LEFT) : (current.from_where = TOP);
	}

	matrix->matrix[row][column] = current;
}

static void count_scores(score_matrix_t * matrix)
{
	assert(NULL != matrix);

	first_init(matrix);

	for (size_t row = 1; row < matrix->height; row++)
	{
		for (size_t column = 1; column < matrix->width; column++)
		{
			count_cell(matrix, row, column);
		}
	}
}

static void traceback(score_matrix_t * matrix, size_t * seq_idx, size_t * gaps, size_t * identity)
{
	assert(NULL != matrix);

	size_t row = matrix->height - 1;
	size_t column = matrix->width - 1;
	char top_char = 't';
	char left_char = 'l';

	while (1)
	{
		if (0 == row && 0 == column)
		{
			return;
		}
		cell_t current = matrix->matrix[row][column];

		if (DIAG == current.from_where)
		{
			assert(column != 0);
			assert(row != 0);

			top_char = matrix->first_input[column - 1];
			left_char = matrix->second_input[row - 1];
			if (top_char == left_char)
			{
				(*identity)++;
				matrix->inter_str[*seq_idx] = '|';
			}
			else
			{
				matrix->inter_str[*seq_idx] = '.';
			}
			matrix->first_seq[*seq_idx] = top_char;
			matrix->second_seq[*seq_idx] = left_char;
			(*seq_idx)--;
			row--;
			column--;
		}
		else if (TOP == current.from_where)
		{
			assert(current.top_gaps > 0);
			assert(row > 0);

			for (size_t i = 0; i < current.top_gaps; i++)
			{
				left_char = matrix->second_input[row - 1];
				matrix->inter_str[*seq_idx] = ' ';
				matrix->first_seq[*seq_idx] = '-';
				matrix->second_seq[*seq_idx] = left_char;
				(*gaps)++;
				(*seq_idx)--;
				row--;
			}
		}
		else
		{
			assert(current.left_gaps > 0);
			assert(column > 0);

			for (size_t i = 0; i < current.left_gaps; i++)
			{
				top_char = matrix->first_input[column - 1];
				matrix->inter_str[*seq_idx] = ' ';
				matrix->first_seq[*seq_idx] = top_char;
				matrix->second_seq[*seq_idx] = '-';
				(*gaps)++;
				(*seq_idx)--;
				column--;
			}
		}
	}
}

static void print_alignment(score_matrix_t const * matrix, size_t start_idx, size_t gaps, size_t identity)
{
	assert(NULL != matrix);

	size_t i = matrix->height - 1;
	size_t j = matrix->width - 1;
	size_t current_len = i + j - start_idx;

	printf("\nGap open: %.1lf\n", (double)GAP_OPEN);
	printf("Gap extend: %.1lf\n\n", (double)GAP_EXTEND);
	printf("Length:     %zu\n", current_len);
	printf("Identity:   %zu/%zu\n", identity, current_len);
	printf("Gaps:       %zu/%zu\n", gaps, current_len);
	printf("SCORE: %.1lf\n", MAX3(matrix->matrix[i][j].diag_score, matrix->matrix[i][j].left_score,
								  matrix->matrix[i][j].top_score));
	printf("======================================\n");
	for (size_t idx = start_idx; idx < i + j; idx += SYMBOLS_PER_LINE)
	{
		printf("\t%.*s\n", SYMBOLS_PER_LINE, matrix->first_seq + idx);
		printf("\t%.*s\n", SYMBOLS_PER_LINE, matrix->inter_str + idx);
		printf("\t%.*s\n", SYMBOLS_PER_LINE, matrix->second_seq + idx);
		printf("\n");
	}
}

static char * read_input()
{
	char * buffer = (char *)calloc(BUFFER_SIZE + 1, sizeof(*buffer));
	if (NULL == buffer)
	{
		return NULL;
	}
	size_t idx = 0;
	int ch = ' ';
	while (EOF != (ch = fgetc(stdin)) && ('\n' != ch))
	{
		if ('A' != ch && 'C' != ch && 'T' != ch && 'G' != ch)
		{
			free(buffer);
			return NULL;
		}

		buffer[idx++] = (char)ch;
		if (0 == idx % BUFFER_SIZE)
		{
			buffer = (char *)realloc(buffer, idx * 2);
			if (NULL == buffer)
			{
				free(buffer);
				return NULL;
			}
		}
	}
	buffer[idx++] = '\0';
	buffer = (char *)realloc(buffer, idx);
	return buffer;
}

int align()
{
	char * first_input = read_input();
	if (NULL == first_input)
	{
		return FIRST_INPUT_ERROR;
	}
	char * second_input = read_input();
	if (NULL == second_input)
	{
		free(first_input);
		return SECOND_INPUT_ERROR;
	}

	score_matrix_t * matrix = create_score_matrix(first_input, second_input);
	if (NULL == matrix)
	{
		free(first_input);
		free(second_input);
		return CREATING_MATRIX_ERROR;
	}

	size_t start_idx = matrix->height - 1 + matrix->width - 1 - 1;
	size_t gaps = 0;
	size_t identity = 0;

	count_scores(matrix);
	traceback(matrix, &start_idx, &gaps, &identity);

	print_alignment(matrix, start_idx + 1, gaps, identity);
	free_scoring_matrix(matrix);
	return OK;

}