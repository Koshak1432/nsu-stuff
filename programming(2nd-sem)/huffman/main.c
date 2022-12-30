#include "sort.h"
#include "huffman_encode.h"
#include "huffman_decode.h"

const char * const messages[] = {
		"OK",
		"lack of arguments",
		"can't open src file",
		"can't open output file",
		"can't open decoded file",
		"encoding error",
		"decoding error"
};

enum errors
{
	OK,
	LACK_OF_ARGUMENTS_ERROR,
	OPENING_SRC_ERROR,
	OPENING_OUTPUT_ERROR,
	OPENING_DECODED_ERROR,
	ENCODING_ERROR,
	DECODING_ERROR,
};

int main(int argc, char * argv[])
{
	if (argc < 2)
	{
		fprintf(stderr, "%s\n", messages[LACK_OF_ARGUMENTS_ERROR]);
		return LACK_OF_ARGUMENTS_ERROR;
	}
	FILE * input_file = fopen(argv[1], "r+b");
	if (NULL == input_file)
	{
		fprintf(stderr, "%s\n", messages[OPENING_SRC_ERROR]);
		return OPENING_SRC_ERROR;
	}
	FILE * output_file = fopen("output.txt", "w+");
	if (NULL == output_file)
	{
		fprintf(stderr, "%s\n", messages[OPENING_OUTPUT_ERROR]);
		return OPENING_OUTPUT_ERROR;
	}
	size_t len = 0;

	if (encode(input_file, output_file, &len))
	{
		fprintf(stderr, "%s\n", messages[ENCODING_ERROR]);
		fclose(input_file);
		fclose(output_file);
		return ENCODING_ERROR;
	}
	fclose(input_file);

	if (0 == len)
	{
		fprintf(stdout, "there is an empty file\n");
		fclose(output_file);
		return OK;
	}

	FILE * decoded = fopen("decoded.txt", "wb");
	if (NULL == decoded)
	{
		fprintf(stderr, "%s\n", messages[OPENING_DECODED_ERROR]);
		fclose(output_file);
		return OPENING_DECODED_ERROR;
	}

	rewind(output_file);
	if (decode(output_file, decoded, len))
	{
		fprintf(stderr, "%s\n", messages[DECODING_ERROR]);
		fclose(output_file);
		fclose(decoded);
		return DECODING_ERROR;
	}

	fclose(output_file);
	fclose(decoded);
	return 0;
}