#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <errno.h>

#define exp_bias 1023 //смещение
#define mask_of_exponent 0x7ff //11 бит (111 1111 1111)
#define mask_of_fraction 0xfffffffffffff //52 бита (f = 4 bit -> 52 bit = 4 * 13)
#define sign_bits 63
#define fraction_bits 52
#define exponent_bits 11
#define max_value_fraction 0x1fffffffffffff  //макс значение мантиссы 2^53 - 1
#define max_size_exp 4
#define max_size_fraction 16
#define max_size_double 398

typedef struct
{
    char storage[400];
    int64_t integer;
    double doub_number;
} massive_t;

//param == how many bits need to print
void print_bits(int64_t num, int const param)
{
    for (int i = 0; i < param; i++)
    {
        printf("%lld", (num >> (param - i - 1)) & 1); // сначала первый, потом второй и т.д.
    }
}

void print_sign(const int sign)
{
    printf("sign = %d(%c)\n", sign, sign == 1 ? '-': '+');
}

void print_binary(const int64_t exp, const int64_t fract)
{
    printf("AS BINARY:\n");
    printf("exponent == ");
    print_bits(exp, exponent_bits);
    printf("\n");
    printf("fraction == ");
    print_bits(fract, fraction_bits);
    printf("\n");
}

void print_decimal(const int64_t exp, const int64_t fract)
{
    printf("AS DECIMAL:\n");
    printf("exponent == %lld\n", exp - exp_bias);
    printf("fraction == %lld\n", fract);
}

int64_t doub_to_int(const double num)
{
    char *d = (char *) &num;
    int64_t result;
    char *i = (char *) &result;
    memcpy(i, d, 8);
    return result;
}

double int_to_dub(int sign, int64_t exp, int64_t fraction)
{
    //sign << 63  exp << 52
    uint64_t integ = (((uint64_t)sign << sign_bits) | (exp << fraction_bits)) | fraction;
    char *i = (char *) &integ;
    double result;
    char *d = (char *) &result;
    memcpy(d, i, 8);
    return result;
}

void clear_massive(massive_t *arr, const int size)
{
    for (int i = 0; i < size; i++)
    {
        arr->storage[i] = '\0';
    }
}

void remove_element(char *arr, const int index,const int max)
{
    for(int i = index; i < max; i++)
    {
       arr[i] = arr[i + 1];
    }
}

void skip_line()
{
    while (1)
    {
        int ch = getchar();
        if (ch == '\n')
        {
            break;
        }
        if (ch == ' ')
        {
            continue;
        }
        if (ch == EOF)
        {
            break;
        }
    }
}

int determine_sign(const char sign)
{
    return ('+' == sign) ? 0 : 1;
}

massive_t read(const char object) //1-d, 2-e, 3-f
{
    while (1)
        {
            massive_t str;
            int max_size, idx;
            int len;
            if (object == 'd')
            {
                printf("\n");
                printf("Enter a number: ");
                max_size = max_size_double;
            }
            else if (object == 'e')
            {
                printf("\n");
                printf("Enter an exponent: ");
                max_size = max_size_exp + 1;  //+1 на случай минуса
            }
            else
            {
                printf("\n");
                printf("Enter a fraction: ");
                max_size = max_size_fraction;
            }

            while (1)
            {
                //+2 потому что fgets считывает до max + 1 символов, в конец пихает терминатор
                //таким образом, она считывает не больше, чем возможно
                if (NULL == fgets(str.storage, max_size + 2, stdin))
                {
                    len = 0;
                    break;
                }
                int i = 0;
                len = strlen(str.storage);
                // если строка заполнена, а в конце нет перевода строки
                if (len == max_size + 1 && str.storage[len - 1] != '\n')
                {
                    // слишком большая строка
                    len = 0;
                    skip_line(); // чистит весь stdin
                    break;
                }//если конечный символ - \n, то удаляет его
                if ((str.storage[len - 1]) == '\n')
                {
                    str.storage[len-- - 1] = '\0'; //длина уменьшилась
                }
                //пока не конец строки
                while (str.storage[i] != '\0')
                {
                    if (str.storage[i] == ' ')
                    {
                        remove_element(str.storage, i, max_size);
                        len--;  // длина уменьшается
                        continue;
                    }
                    i++;
                }
                idx = i;
                break;
            }
            if (len == 0) //если пустая строка
            {
                fprintf(stderr, "can't read a number (invalid input)\n\n");
                continue;
            }

            char *endPtr; // указывает на последний обработанный байт
                          // в конечном итоге у меня должен указывать на '\0'
            if (object != 'd') //если не double, пихаю в структуру
            {
                long long number = strtoll(str.storage, &endPtr, 10);
                str.integer = (int64_t) number;
            }
            else
            {
                str.doub_number = strtod(str.storage, &endPtr);
            }
            if (errno == ERANGE) // если значение в number слишком маленькое или большое
            {
                clear_massive(&str, len);
                fprintf(stderr, "number out of limit\n\n");
                continue;
            }


            if (endPtr == &str.storage[idx]) //должен указывать на \0
            {
                if (object == 'd')
                {
                    return str;
                }
                if (object == 'e')
                {
                    if (str.integer < -exp_bias || str.integer > exp_bias + 1)  //от -1023 до 1024
                    {
                        fprintf(stderr, "the number of exponent is too large\n\n");
                        clear_massive(&str, len);
                        continue;
                    }
                }
                else
                {
                    if (str.integer > max_value_fraction) // макс значение мантиссы 2^53 - 1
                    {
                        fprintf(stderr, "the number of fraction is too large\n\n");
                        clear_massive(&str, len);
                        continue;
                    }
                    if (str.integer < 0)
                    {
                        fprintf(stderr, "invalid fraction\n\n");
                        clear_massive(&str, len);
                        continue;
                    }
                }
                return str;
            }
            clear_massive(&str, len);
            fprintf(stderr, "invalid number\n\n");
        }
}

char read_sign()
{
    while (1)
        {
            printf("Enter a sign: ");
            massive_t str;
            while (1)
            {
                if (NULL == fgets(str.storage, 3, stdin))
                {
                    fprintf(stderr, "can't read a number\n\n");
                    break;
                }
                int len = strlen(str.storage);
                if (len == 2 && str.storage[1] != '\n')
                {
                    // слишком большая строка
                    fprintf(stderr, "your input is too large\n\n");
                    skip_line();
                    break;
                }
                if (str.storage[0] == '+' || str.storage[0] == '-')
                {
                    return str.storage[0];
                }
                fprintf(stderr, "can't read a number\n\n");
                clear_massive(&str, len);
                break;
            }
        }
}

void run_fromd()
{
    while(1)
    {
        double digit = read('d').doub_number;
        int64_t bits = doub_to_int(digit);

        uint64_t fract = bits & mask_of_fraction;
        int64_t exp = ((bits >> fraction_bits) & mask_of_exponent);  //убираю биты мантиссы
        int sign = (bits >> sign_bits) & 1;

        print_sign(sign);
        print_binary(exp, fract);
        print_decimal(exp, fract);
    }
}

int run_tod()
{
    while(1)
    {
        int sign = determine_sign(read_sign());
        int64_t exp = read('e').integer;
        int64_t fraction = read('f').integer;
        exp += exp_bias;

        printf("sign = %d(%c)\n", sign, (sign == 0) ? '+' : '-');
        printf("AS DECIMAL: %lf\n", int_to_dub(sign, exp, fraction));
        print_binary(exp, fraction);
    }
}

int main(int argc, char *argv[])
{
    if (argc < 2)
    {
        fprintf(stderr, "enter the key\n\n");
        return -1;
    }
    if (strcmp(argv[1], "-fromd") == 0)
    {
        run_fromd();
    }
    else if(strcmp(argv[1], "-tod") == 0)
    {
        run_tod();
    }
    else
    {
        fprintf(stderr, "incorrect key");
        return -1;
    }
    return 0;
}
