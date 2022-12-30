#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

#define TABLE_SIZE 10
#define LEN_OF_NAME 256

typedef struct data_
{
    size_t height;
    size_t weight;
} data;

typedef struct elem_
{
    char name[LEN_OF_NAME]; //имя студента
    size_t busy;   //0 - свобода, 1-занят, 2 - удалён    мб енам сюда прикрутить
    data data_of_student;
} student;

typedef struct hash_
{
    size_t capacity; //?????????????
    size_t size;
    student * student_data;
} hash_table;

void fill_free(student * arr, size_t len)
{
    assert(NULL != arr);
    for (size_t i = 0; i < len; i++)
    {
        arr[i].busy = 0;
    }
}

hash_table create_table(size_t size)
{
    hash_table table;
    table.student_data = (student *)calloc(TABLE_SIZE, sizeof(*table.student_data));
    table.size = size;
    fill_free(table.student_data, size);
    return table;
}

size_t hash(char const * name, size_t len)
{
    size_t strl = strlen(name);
    size_t res = 0;
    for (size_t i = 0; i < strl; i++)
    {
        res += name[i] * name[i];
        res %= len;
    }
    return res;
}
size_t find_idx(hash_table * table, char * name)
{
    size_t name_hash = hash(name, table->size);
    size_t index = name_hash;
    while (table->student_data[index].busy != 0)
    {
        index = (index + 1) % table->size;
    }
    printf("vernul %zu\n", index);
    return index;
}

void insert_to_table(hash_table *table, char *name, size_t weight, size_t height)
{
    size_t idx = find_idx(table, name);
    printf("idx == %zu, name == %s\n", idx, name);
    printf("DO PRISVAIVANIY\n");

    printf("table->student_data[idx].busy == %zu\n", table->student_data[idx].busy);
    printf("table->student_data[idx].name == %s\n", table->student_data[idx].name);
    printf("table->student_data[idx].data_of_student.height == %zu\n", table->student_data[idx].data_of_student.height);
    printf("table->student_data[idx].data_of_student.weight == %zu\n", table->student_data[idx].data_of_student.weight);

    printf("POSLE PRISVAIVANIY\n");

    table->student_data[idx].busy = 1;
    snprintf(table->student_data[idx].name, LEN_OF_NAME, "%s", name);
    table->student_data[idx].data_of_student.height = height;
    table->student_data[idx].data_of_student.weight = weight;

    printf("table->student_data[idx].busy == %zu\n", table->student_data[idx].busy);
    printf("table->student_data[idx].name == %s\n", table->student_data[idx].name);
    printf("table->student_data[idx].data_of_student.height == %zu\n", table->student_data[idx].data_of_student.height);
    printf("table->student_data[idx].data_of_student.weight == %zu\n", table->student_data[idx].data_of_student.weight);

    table->capacity++;

    printf("_______________________________________________________\n");
    printf("PREV IDX == %zu\n", idx - 1);
    printf("table->student_data[idx].name == %s\n", table->student_data[idx].name);
    printf("table->student_data[idx - 1].name == %s\n", table->student_data[idx - 1].name);
    printf("table->student_data[idx - 1].data_of_student.height == %zu\n", table->student_data[idx - 1].data_of_student.height);
    printf("table->student_data[idx - 1].data_of_student.weight == %zu\n", table->student_data[idx - 1].data_of_student.weight);
    printf("table->student_data[idx - 1].busy == %zu\n", table->student_data[idx - 1].busy);
    printf("END\n");
    printf("\n");
}

void print_table(hash_table * table)
{
    for (size_t i = 0; i < TABLE_SIZE; i++)
    {
        if (table->student_data[i].busy == 0)
        {
            printf("\t%zu\t-------\n", i);
        }
        else
        {

            printf("\t%zu\t%s\n", i, table->student_data[i].name);
        }
    }
}
int main()
{
    FILE * f = fopen("1.txt", "r");
    if (NULL == f)
    {
        fprintf(stderr, "can't open the file!\n");
        return 1;
    }

    hash_table table = create_table(TABLE_SIZE);

    size_t weight;
    size_t height;
    char str[LEN_OF_NAME] = {0};

    while (3 == fscanf(f, "%255s %zu %zu", str, &weight, &height))
    {
        insert_to_table(&table, str, weight, height);
    }

    print_table(&table);

    return 0;
}
