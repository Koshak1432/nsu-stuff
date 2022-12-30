#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>
#include <stdbool.h>

#define X 8
#define Y 8
#define MAX_POSSIBLE_MOVES 8
#define END 1
#define NOT_END 0

static bool move_possible(int x, int y, int ** const board)
{
    assert(NULL != board);

    return (x >= 0 && y >= 0 && x < X && y < Y && board[y][x] == 0);
}

static size_t find_min(size_t * const arr_possible_moves)
{
    assert(NULL != arr_possible_moves);

    size_t minimal = 9;
    size_t imin = 0;

    for (size_t i = 0; i < MAX_POSSIBLE_MOVES; i++)
    {
        if (arr_possible_moves[i] <= minimal && arr_possible_moves[i] != 0)
        {
            imin = i;
            minimal = arr_possible_moves[i];
        }
    }
    return imin;
}

static void print_board(int ** const board, size_t x, size_t y)
{
    assert(NULL != board);

    for (size_t i = 0; i < y; i++)
    {
      for (size_t j = 0; j < x; j++)
      {
        printf("%d ", board[i][j]);
      }
      printf("\n");
    }
}

static int ** create_board(size_t x, size_t y)
{
    int ** board = (int **)calloc(y, sizeof(*board));
    if (NULL == board)
    {
        return NULL;
    }

    for (size_t i = 0; i < y; i++)
    {
        board[i] = (int *)calloc(x, sizeof(*board[i]));
        if (NULL == board[i])
        {
            for (size_t j = 0; j < i; j++)
            {
                free(board[j]);
            }

            free(board);
            return NULL;
        }
    }

    return board;
}

static void free_board(int ** board, size_t y)
{
    assert(NULL != board);

    for (size_t i = 0; i < y; i++)
    {
        free(board[i]);
    }
    free(board);
}

static bool find_ham_way_horse(int ** board, int horse_x, int horse_y, size_t move_count,
            int const moves[MAX_POSSIBLE_MOVES][2], bool *end)
{
    assert(NULL != board);

    if (*end)
    {
        return true;
    }
    board[horse_y][horse_x] = move_count++;

    if (move_count > X * Y)
    {
        printf("WIN!\n\n");
        print_board(board, X, Y);
        *end = true;
        return true;
    }

    size_t arr_possible_moves[MAX_POSSIBLE_MOVES] = {0};

    for (size_t i = 0; i < MAX_POSSIBLE_MOVES; i++)
    {
        int delta_x = horse_x + moves[i][0];
        int delta_y = horse_y + moves[i][1];

        if (move_possible(delta_x, delta_y, board) != 1)
        {
            continue;
        }
        if (move_count == X * Y)
        {
            arr_possible_moves[i] = 1;
            break;
        }
        size_t count_possible_moves = 0;

        for (size_t j = 0; j < MAX_POSSIBLE_MOVES; j++)
        {
            int delta_x2 = delta_x + moves[j][0];
            int delta_y2 = delta_y + moves[j][1];

            if (move_possible(delta_x2, delta_y2, board))
            {
                count_possible_moves++;
            }
        }
        arr_possible_moves[i] = count_possible_moves;
    }

    size_t best_move = find_min(arr_possible_moves);
    int best_delta_x = horse_x + moves[best_move][0];
    int best_delta_y = horse_y + moves[best_move][1];
    for (size_t i = 0; i < MAX_POSSIBLE_MOVES; i++)
    {
        if (move_possible(best_delta_x, best_delta_y, board)
            && find_ham_way_horse(board, best_delta_x, best_delta_y, move_count, moves, end))
        // move_pos for arr_pos = {0}
        {
            return true;
        }
        else
        {
            arr_possible_moves[best_move] = 0;
            best_move = find_min(arr_possible_moves);
            best_delta_x = horse_x + moves[best_move][0];
            best_delta_y = horse_y + moves[best_move][1];
        }
    }


    board[horse_y][horse_x] = 0;
    return false;
}
int main()
{
    int const moves[MAX_POSSIBLE_MOVES][2] = {{1, -2}, {2, -1}, {2, 1}, {1, 2},
                                             {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}};
    int horse_x = 0;
    int horse_y = 0;
    int move_count = 1;
    bool end = false;
    int ** board = create_board(X, Y);
    if (NULL == board)
    {
        fprintf(stderr, "Can't allocate memory for board.\n");
        return -1;
    }

    if (!find_ham_way_horse(board, horse_x, horse_y, move_count, moves, &end))
    {
        printf("can't find the way :( \n");
    }
    free_board(board, Y);
    return 0;
}
