#include "list.h"

#include <stdio.h>
#include <assert.h>
#include <stdlib.h>
#include <string.h>

static list_t *createNode(const char *str) {
	list_t *node = (list_t *)calloc(1, sizeof(*node));
	if (NULL == node) {
		return NULL;
	}
	size_t len = strlen(str) + 1;
	node->val = (char *)calloc(len, sizeof(char));
	if (NULL == node->val) {
		free(node);
		return NULL;
	}
	strncpy(node->val, str, len); //null byte also
	node->next = NULL;
	return node;
}

bool insertNode(list_t **list, const char *str) {
	assert(NULL != list);
	list_t *tmp = *list;
	list_t *node = createNode(str);
	if (NULL == node) {
		return false;
	}
	if (NULL == *list) {
		*list = node;
		return true;
	}

	while (NULL != tmp->next) {
		tmp = tmp->next;
	}
	tmp->next = node;
	return true;
}

void printList(list_t *list) {
	list_t *tmp = list;
	while (NULL != tmp) {
		printf("%s\n", tmp->val);
		tmp = tmp->next;
	}
	printf("\n");
}

void freeList(list_t *list) {
	list_t *tmp = NULL;
	while (NULL != list) {
		tmp = list;
		list = list->next;
		free(tmp->val);
		free(tmp);
	}
}
