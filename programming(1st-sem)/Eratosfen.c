#include <stdio.h>
#include <stdlib.h>

int prime(int n)
{
    int limit = n * n * 2;
    int *a = (int *)calloc(limit, sizeof(int));
    for (int i = 0; i < limit; i++)
    {
        a[i]=i;
    }
    int i = 2;
    a[1]=0;
    for (; i * i < limit; i++)
    {
        if(a[i] != 0)
        {
            for (int k = i * i; k < limit; k+=i)
            {
                a[k]=0;
            }
        }
    }
    int count = 0;
    for (i = 0; i < limit; i++)
    {
        if (a[i]!= 0)
        {
            count++;
        }
        if (count == n)
        {
            break;
        }
    }
    int result = a[i];
    free(a);
    return result;
}
int main()
{
    printf("%d",prime(10000));
    return 0;
}
