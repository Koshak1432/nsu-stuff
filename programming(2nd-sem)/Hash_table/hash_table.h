#ifndef HASH_TABLE_H_INCLUDED
#define HASH_TABLE_H_INCLUDED
#include <stdint.h>

#define NAME_LEN 100

typedef struct
{
    char name[NAME_LEN];
    size_t height;
    size_t weight;
} student_t;

typedef struct linked_list
{
    student_t *student;
    struct linked_list *next;
} list_t;

typedef struct
{
    size_t table_size;
    size_t load;
    list_t **students;
} hash_tbl_t;

hash_tbl_t * create_tbl(size_t size);

void free_table(hash_tbl_t * table);

void table_delete(hash_tbl_t const * table, char const *name);

void print_table(hash_tbl_t const *table);

void table_insert(hash_tbl_t ** table, char * name, size_t height, size_t weight);

student_t * table_search(hash_tbl_t const * table, char const * name);

void print_if_exists(hash_tbl_t const * table, char const * name);

#endif // HASH_TABLE_H_INCLUDED
