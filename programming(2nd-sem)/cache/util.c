#include "util.h"

#include <stdlib.h>
#include <stdio.h>
#include <ctype.h>
#include <errno.h>

void skip()
{
  while(1)
  {
    int ch = getchar();
    if (EOF == ch || '\n' == ch)
    {
      return;
    }
  }
}

bool read_numbers(long long int * result)
{
  //В long long влезет 20 цифр
  char str[21] = {'\0'}; //[20] == '\0'
  size_t len = 0;

  for (int i = 0; i < 20; i++)
  {
    int ch = fgetchar();
    if (ch == EOF || !isdigit(ch))
    {
      str[i] = '\0';
      break;
    }
    str[i] = (char)ch;
    len++;
  }

  if (0 == len)
  {
    return false;
  }
  char *end;
  long long int number = strtoll(str, &end, 10);

  if (end != &str[len] || errno == ERANGE)
  {
    return false;
  }

  *result = number;
  return true;
}
