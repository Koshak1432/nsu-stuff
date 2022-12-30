#include "graph.h"
#include "util.h"

#define START_SIZE 100

const char * const messages[] = {
		"OK",
		"Lack of arguments",
		"Can't create table(graph)",
		"Can't open file",
		"Reading file error",
		"Invalid input",
		"Finding min way error"
};

enum errors
{
	OK,
	LACK_OF_ARGUMENTS_ERROR,
	CREATE_TABLE_ERROR,
	OPENING_FILE_ERROR,
	READING_FILE_ERROR,
	INVALID_INPUT_ERROR,
	FINDING_MIN_WAY_ERROR,
};

int main(int argc, char * argv[])
{
	if (argc < 2)
	{
		fprintf(stderr, "%s\n", messages[LACK_OF_ARGUMENTS_ERROR]);
		return LACK_OF_ARGUMENTS_ERROR;
	}

	hash_tbl_t * table = create_tbl(START_SIZE);
	if (NULL == table)
	{
		fprintf(stderr, "%s\n", messages[CREATE_TABLE_ERROR]);
		return CREATE_TABLE_ERROR;
	}

	FILE * input = fopen(argv[1], "r");
	if (NULL == input)
	{
		free_table(table);
		fprintf(stderr, "%s\n", messages[OPENING_FILE_ERROR]);
		return OPENING_FILE_ERROR;
	}

	if (read_file(table, input))
	{
		fclose(input);
		free_table(table);
		fprintf(stderr, "%s\n", messages[READING_FILE_ERROR]);
		return READING_FILE_ERROR;
	}
	fclose(input);

	bool error = false;
	long long int src_id = 0;
	long long int dst_id = 0;

	printf("Enter src and dst: ");
	if (read_numbers(&src_id))
	{
		free_table(table);
		fprintf(stderr, "%s\n", messages[INVALID_INPUT_ERROR]);
		return INVALID_INPUT_ERROR;
	}
	if (read_numbers(&dst_id))
	{
		free_table(table);
		fprintf(stderr, "%s\n", messages[INVALID_INPUT_ERROR]);
		return INVALID_INPUT_ERROR;
	}
	printf("\n");

	double min_dist = find_min_path(table, src_id, dst_id, &error);
	if (error)
	{
		free_table(table);
		fprintf(stderr, "%s\n", messages[FINDING_MIN_WAY_ERROR]);
		return FINDING_MIN_WAY_ERROR;
	}

	print_result(table, min_dist);
	free_table(table);
	return OK;
}
