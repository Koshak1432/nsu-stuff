#include <stdio.h>
#include "hash_table.h"

#define SIZE 10

int main()
{
    hash_tbl_t * table = create_tbl(SIZE);
    table_insert(&table, "John Kek", 160, 55);
    table_insert(&table, "Clare johnson", 180, 47);
    table_insert(&table, "Petr the first", 201, 80);
    table_insert(&table, "Ivan Ivanov", 177, 90);
    table_insert(&table, "Zuza Adamovich", 160, 122);
    table_insert(&table, "Lisa EEEEM", 160, 100);
    table_insert(&table, "Rim Charles", 160, 65);
    table_insert(&table, "Vlad Ugr", 160, 85);

    print_if_exists(table, "Ivan Ivanov");
    print_if_exists(table, "Lisa EEEEM");
    print_if_exists(table, "Vlad Ugr");
    print_if_exists(table, "Clare johnson");
    print_if_exists(table, "Rim Charles");
    print_table(table);
    table_delete(table, "Ivan Ivanov");
    table_delete(table, "Ivan Ivanov");
    print_if_exists(table, "Ivan Ivanov");
    print_table(table);
    free_table(table);
    return 0;
}
