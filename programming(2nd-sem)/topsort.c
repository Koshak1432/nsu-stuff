#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <assert.h>
#include <string.h>

enum name
{
    N,
    Y,
    MAX = 9
};

static void print_top(int * arr)
{
    for (int i = 0; i < MAX; i++)
    {
        printf("%d ", arr[i] + 1);
    }
}

static size_t find_sum(int const matrix[MAX][MAX], size_t y)
{
    size_t sum = 0;
    for (size_t x = 0; x < MAX; x++)
    {
        sum += matrix[x][y];
    }
    return sum;
}

static void table_run(int matrix[MAX][MAX], bool * visited, bool * cycle, int * top_order, int arr_top[MAX])
{
    assert(NULL != visited);
    for (size_t y = 0; y < MAX; y++)
    {
        size_t count_iter = MAX - y;
        if (!visited[y])
        {
            if (0 == find_sum(matrix, y))
            {
                arr_top[(*top_order)++] = y;
                visited[y] = true;
                memset(matrix[y], 0, MAX * sizeof(**matrix));
            }
            else if (--count_iter == 0)
            {
                *cycle = true;
                return;
            }
        }
    }
}

int main()
{
    // first[] -- row | second[] -- column
    int matrix[MAX][MAX] = {
    {N, Y, N, N, N, Y, N, N, N},
    {N, N, N, N, Y, N, N, N, N},
    {Y, N, N, N, N, Y, N, N, N},
    {N, N, N, N, N, N, N, N, N},
    {N, N, N, Y, N, N, N, N, N},
    {N, N, N, Y, N, N, N, N, N},
    {Y, N, N, N, N, N, N, N, N},
    {Y, N, N, N, N, N, N, N, N},
    {N, N, Y, N, N, N, N, N, N}};

    bool visited[MAX] = {false};
    bool cycle = false;
    int arr_top[MAX] = {0};
    int top_order = 0;

    for (size_t i = 0; i < MAX; i++)
    {
        table_run(matrix, visited, &cycle, &top_order, arr_top);
        if (cycle)
        {
            printf("THERE IS A CYCLE!!!\n");
            return 0;
        }
    }
    print_top(arr_top);
    return 0;
}
