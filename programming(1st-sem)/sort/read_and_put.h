#ifndef READ_AND_PUT_H_INCLUDED
#define READ_AND_PUT_H_INCLUDED

void make_sorted_files(int *count_f, int *error, int how_many_in_1_file, FILE * mainf, int (*cmp)(const void *, const void *));

void make_result(int count_files, int *err, int (*cmp)(const void *, const void *));

#endif // READ_AND_PUT_H_INCLUDED
