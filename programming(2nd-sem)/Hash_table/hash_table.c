#include "hash_table.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <stdbool.h>

static list_t ** create_students(hash_tbl_t const * table)
{
    assert(NULL != table);

    list_t ** students = (list_t **)calloc(table->table_size, sizeof(*students));
    if (NULL == students)
    {
        return NULL;
    }

    for (size_t i = 0; i < table->table_size; i++)
    {
        students[i] = NULL;
    }
    return students;
}

static void free_list(list_t * list)
{
    list_t *tmp = list;

    while (NULL != list)
    {
        tmp = list;
        list = list->next;
        if (NULL != tmp->student)
        {
            printf("freeshnul student %s\n", tmp->student->name);
            free(tmp->student);
        }
        free(tmp);
    }
}

static void free_students(hash_tbl_t const * table)
{
    assert(NULL != table);

    for (size_t i = 0; i < table->table_size; i++)
    {
        if (table->students[i] != NULL)
        {
            free_list(table->students[i]);
        }
    }
    free(table->students);
}

hash_tbl_t * create_tbl(size_t size)
{
    assert(size > 0);

    hash_tbl_t * table = (hash_tbl_t *)calloc(1, sizeof(*table));
    if (NULL == table)
    {
        return NULL;
    }
    table->table_size = size;
    table->load = 0;
    table->students = create_students(table);
    if (NULL == table->students)
    {
        return NULL;
    }
    return table;
}

void free_table(hash_tbl_t * table)
{
    assert(NULL != table);

    free_students(table);
    free(table);
}

static student_t *create_student(char const * name, size_t height, size_t weight)
{
    assert(NULL != name);

    student_t *student = (student_t *)calloc(1, sizeof(*student));
    if (NULL == student)
    {
        return NULL;
    }
    student->height = height;
    student->weight = weight;
    strcpy(student->name, name);
    return student;
}

static size_t hash(char const * name, size_t len)
{
    assert(NULL != name);
    assert(len > 0);

    size_t strl = strlen(name);
    size_t res = 0;
    size_t prime_number = 7919;

    for (size_t i = 0; i < strl; i++)
    {
        res += name[i] * name[i] + prime_number;
        res %= len;
    }
    return res;
}

static list_t *create_element(student_t * student)  //list elem
{
    assert(NULL != student);

    list_t *elem = (list_t *)malloc(sizeof(*elem));
    elem->student = student;
    elem->next = NULL;
    return elem;
}

static list_t * add_elem(list_t *list, student_t * student) //to head (add 3 to 1->2 == 3->1->2)
{
    assert(NULL != student);

    list_t *new_elem = create_element(student);
    new_elem->next = list;
    list = new_elem;
    printf("added student %s\n", list->student->name);
    printf("----------------------------\n");
    return list;
}

void table_delete(hash_tbl_t const * table, char const *name)
{
    assert(NULL != table); //or something else

    size_t idx = hash(name, table->table_size);
    printf("deleting student on idx == %zu\n", idx);
    list_t * prev = table->students[idx];

    if (NULL == prev)
    {
        printf("student %s isn't in the table!\n", name);
        return;
    }

    if (strcmp(name, prev->student->name) == 0)
    {
        free_list(prev);
        printf("student %s has been deleted!\n", name);
        return;
    }

    list_t * tmp = table->students[idx]->next;

    while(NULL != tmp)
    {
        if (strcmp(name, tmp->student->name) == 0)
        {
            list_t *after_tmp = tmp->next;
            free(tmp);
            prev->next = after_tmp;
            printf("student %s has been deleted!\n", name);
            return;
        }
        prev = prev->next;
        tmp = tmp->next;
    }
    printf("student %s isn't in the table!\n", name);
}

void print_table(hash_tbl_t const *table)
{
    assert(NULL != table);

    for (size_t i = 0; i < table->table_size; i++)
    {
        printf("idx == %zu : ", i);
        list_t * elem = table->students[i];
        while (NULL != elem)
        {
            printf("Name == %s, height == %zu, weight == %zu ",
                    elem->student->name, elem->student->height, elem->student->weight);
            if (NULL != elem->next)
            {
                printf(" --> ");
            }
            elem = elem->next;
        }
        printf("\n");
    }
}

static hash_tbl_t * table_rehash(hash_tbl_t * table)
{
    assert(NULL != table);

    hash_tbl_t * new_table = create_tbl(table->table_size * 2);
    if (NULL == new_table)
    {
        return NULL;
    }

    for (size_t i = 0; i < table->table_size; i++)
    {
        list_t * elem = table->students[i];
        while (NULL != elem)
        {
            table_insert(&new_table, elem->student->name,
                         elem->student->height, elem->student->weight);
            elem = elem->next;
        }
    }
    free_table(table);
    return new_table;
}

void table_insert(hash_tbl_t ** table, char * name, size_t height, size_t weight)
{
    assert(NULL != *table);

    student_t * student = create_student(name, height, weight);
    size_t idx = hash(name, (*table)->table_size);
    list_t *tmp = (*table)->students[idx];

    while (NULL != tmp)
    {
        if (0 == strcmp(name, tmp->student->name))
        {
            (*table)->students[idx]->student->height = student->height;
            (*table)->students[idx]->student->weight = student->weight;
            return;
        }
        tmp = tmp->next;
    }
    if ((*table)->load == (*table)->table_size)
    {
        *table = table_rehash(*table);
        if (NULL == *table)
        {
            fprintf(stderr, "can't create new table\n");
            return;
        }
        idx = hash(name, (*table)->table_size);
    }
    (*table)->students[idx] = add_elem((*table)->students[idx], student);
    (*table)->load++;
}

student_t * table_search(hash_tbl_t const * table, char const * name)
{
    assert(NULL != table);
    assert(NULL != name);

    size_t idx = hash(name, table->table_size);
    printf("table search name %s, idx == %zu\n", name, idx);
    list_t *elem = table->students[idx];
    if (NULL == elem)
    {
        return NULL;
    }

    while(NULL != elem)
    {
        if (strcmp(name, elem->student->name) == 0)
        {
            return elem->student;
        }
        elem = elem->next;
    }
    return NULL;
}

void print_if_exists(hash_tbl_t const * table, char const * name)
{
    assert(NULL != table);
    assert(NULL != name);

    student_t * student = table_search(table, name);
    if (NULL == student)
    {
        printf("Doesn't exist\n");
        return;
    }
    printf("Name == %s, height == %zu, weight == %zu\n",
           name, student->height, student->weight);
}
