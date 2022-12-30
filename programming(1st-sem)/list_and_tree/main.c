#include "linked_list.h"
#include "tree.h"

int main(int argc, char *argv[])
{
    if (argc < 2)
    {
        fprintf(stderr, "enter the name of file!\n");
        return 1;
    }
    FILE *f = fopen(argv[1], "r");   //сюда аргв[1] + проверки на аргумент

    if (NULL == f)
    {
        fprintf(stderr, "can't open the file!\n");
        return 2;
    }
    size_t len = 0;
    list_t *L = read_from_file(f, &len);
    merge_sort(&L, len);

    tree_t * res = NULL;
    make_tree_and_free_list(&res, L, len);
    print_tree(res);
    free_tree(res);
    return 0;
}
