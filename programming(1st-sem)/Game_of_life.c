#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <assert.h>
#include <stdbool.h>
#include <ctype.h>

typedef struct
{
    size_t width;
    size_t height;
    int **storage;
} field_t;

typedef struct
{
    field_t current;
    field_t next;
} state_t;

typedef struct
{
    bool birth[9];
    bool sustain[9];
} rule_t;

field_t create_field(size_t width, size_t height)
{
    assert(width > 0 && height > 0);
    field_t field;
    field.width = width;
    field.height = height;
    field.storage = (int**) calloc(height, sizeof(*field.storage));
    for (size_t i = 0; i < height; i++)
    {
        field.storage[i] = (int*) calloc(width, sizeof(*field.storage[i]));
    }
    return field;
}

void free_field(field_t field)
{
    for (size_t i = 0; i < field.height; i++)
    {
        free(field.storage[i]);
    }
    free(field.storage);
}

// нормализует координату в тороидальном мас.
int wrap(int i, size_t max)
{
    assert(max != 0);
    while (i < 0)
    {
        i += max;
    }
    return i % max;
}

// ретурнит значение ячейки
int get(field_t *field, int x, int y)
{
    assert(NULL != field);
    x = wrap(x, field->width);
    y = wrap(y, field->height);
    return field->storage[y][x];
}

// изменяет значение ячейки
void set(field_t *field, int x, int y, int cell)
{
    assert(NULL != field);
    x = wrap(x, field->width);
    y = wrap(y, field->height);
    field->storage[y][x] = cell;
}
// обменять два поля
void swap(field_t *field_1, field_t *field_2)
{
    assert(NULL != field_1 && NULL != field_2);
    assert(field_1->height == field_2->height && field_1->width == field_2->width);
    int **temp = field_1->storage;
    field_1->storage = field_2->storage;
    field_2->storage = temp;
}

void print_state(state_t *state)
{
    assert(NULL != state);
    for (size_t x = 0; x < state->current.width; x++)
    {
        for (size_t y = 0; y < state->current.height; y++)
        {
            if (get(&state->current, x, y))
            {
                printf("X");
            } else
            {
                printf("-");
            }
        }
        printf("\n");
    }
}

int count_neighbors(field_t *field, int x, int y)
{
    assert(NULL != field);
    int lifeCount = 0;
    for (int i = x - 1; i <= x + 1; i++)
    {
        for (int j = y - 1; j <= y + 1; j++)
        {
            if ((i == x) && (j == y))
            {
                continue;
            }
            if (get(field, i, j))
            {
                lifeCount++;
            }
        }
    }
    return lifeCount;
}

void next_gen(state_t *state)
{
    assert(NULL != state);
    for (size_t x = 0; x < state->current.width; x++)
    {
        for (size_t y = 0; y < state->current.height; y++)
        {
            int neighbors = count_neighbors(&state->current, x, y);
            int cell = get(&state->current, x, y);
            set(&state->next, x, y, cell);
            if (neighbors == 3 && !cell)
            {
                set(&state->next, x, y, 1);
            }
            else if ((neighbors < 2 || neighbors > 3) && cell)
            {
                set(&state->next, x, y, 0);
            }
        }
    }
    swap(&state->current, &state->next);
}

state_t create_state(field_t field)
{
    state_t state;
    state.current = field;
    state.next = create_field(field.width, field.height);
    return state;
}

void free_state(state_t state)
{
    free_field(state.current);
    free_field(state.next);

}

int read_rle(FILE *f, field_t *field, rule_t *rule)
{
    assert(NULL != f && NULL != field);
    int width, height;
    if (fscanf(f, " x = %d, y = %d", &width, &height) < 2)
    {
        return -24;
    };
    char c;
    if (fscanf(f, "%c", &c) != 1)
    {
        return -22;
    }
    if (c == ',')
    {
        fscanf(f, " rule = B");
        if (ferror(f) || feof(f))
        {
            return -40;
        }

        while (1)
        {
            char ch_digit;
            if (fscanf(f, "%c", &ch_digit) != 1)
            {
                return -66;
            }
            if (!isdigit(ch_digit))
            {
                if (ch_digit == '/')
                {
                    break;
                }
                return -11;
            }
            int digit = (int)(ch_digit - '0');
            rule->birth[digit] = true;
        }
    }
    fscanf(f, "S");
    if (ferror(f) || feof(f))
    {
        return -9;
    }
    while (1)
    {
        char ch_digit;
        if (fscanf(f, "%c", &ch_digit) != 1)
        {
            return -8;
        }
        if (!isdigit(ch_digit))
        {
            if (isspace(ch_digit))
            {
                break;
            }
            return -14;
        }
        int digit = (int)(ch_digit - '0');
        rule->sustain[digit] = true;
    }
    fscanf(f, " ");
    *field = create_field(width, height);
    size_t x = 0;
    size_t y = 0;
    while (1)
    {
        int count;
        int met_digit = fscanf(f, "%d", &count);
        if (met_digit == 0)
        {
            count = 1;
        }
        if (count <= 0)
        {
            return -30;
        }
        char ch;
        if (fscanf(f, "%c", &ch) != 1)
        {
            if (met_digit == 0)
            {
                return 0;
            }
            return -20;
        }
        if (ch == 'b' || ch == 'o')
        {
            for (int i = 0; i < count; i++)
            {
                if (x < field->width && y < field->height)
                {
                    set(field, x, y, ch == 'o');
                    x++;
                }
            }
        }
        else if (ch == '$')
        {
            x = 0;
            y += count;
        }
        else if (ch == '\n')
        {
            if (met_digit == 1)
            {
                return -666;
            }
            continue;
        }
        else if (ch == '!')
        {
            break;
        }
        else
        {
            return -1;
        }
    }
    return 0;
}

int main(int argc, char *argv[])
{
    if (argc < 2)
    {
        fprintf (stderr, "give me a file\n");
        return -1;
    }
    FILE *f = fopen(argv[1], "rb");
    if (NULL == f)
    {
        perror("i can't open the file");
        return -2;
    }
    field_t field;
    rule_t rule;
    int error = read_rle(f, &field, &rule);
    if (error != 0)
    {
        fclose(f);
        printf("i can't read the file, error %d", error);
        return -88;
    }
    fclose(f);
    state_t state = create_state(field);
    while (1)
    {
        print_state(&state);
        int ch;
        if ((ch=getchar()) != EOF)
        {
            next_gen(&state);
        }
    }
    return 0;
}
