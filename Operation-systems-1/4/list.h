#ifndef INC_4LAB_LIST_H
#define INC_4LAB_LIST_H

#include <stdbool.h>

typedef struct List{
	struct List *next;
	char *val;
} list_t;

bool insertNode(list_t **list, const char *str);
void printList(list_t *list);
void freeList(list_t *list);

#endif //INC_4LAB_LIST_H
