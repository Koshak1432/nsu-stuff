#include "sort.h"

#include <assert.h>

static void swap_nodes(node_t ** first, node_t ** second)
{
	assert(NULL != first);
	assert(NULL != second);
	assert(NULL != *first);
	assert(NULL != *second);

	node_t * tmp = *first;
	*first = *second;
	*second = tmp;
}

static void partition(node_t ** tree_arr, size_t start, size_t end, size_t * l, size_t * r)
{
	assert(NULL != tree_arr);

	if ((end - start) <= 1)
	{
		if (tree_arr[start]->freq > tree_arr[end]->freq)
		{
			swap_nodes(&tree_arr[start], &tree_arr[end]);
		}
		*l = start;
		*r = end;
		return;
	}
	size_t left = start;
	size_t right = end;
	size_t head = start;
	size_t pivot = tree_arr[(left + right) / 2]->freq;

	while (head <= right)
	{
		if (pivot > tree_arr[head]->freq)
		{
			swap_nodes(&(tree_arr[head]), &(tree_arr[left]));
			left++;
			head++;
		}
		else if (pivot < tree_arr[head]->freq)
		{
			swap_nodes(&tree_arr[head], &tree_arr[right]);
			right--;
		}
		else
		{
			head++;
		}
	}
	// left stands on leftmost pivot
	// right stands on rightmost pivot
	// head stands on the next of right (first > pivot)
	*l = left;
	*r = head;
}

void sort(node_t ** tree_arr, size_t start, size_t end)
{
	assert(NULL != tree_arr);

	if (start < end)
	{
		size_t left = 0;
		size_t right = 0;
		partition(tree_arr, start, end, &left, &right);
		sort(tree_arr, start, left);
		sort(tree_arr, right, end);
	}
}

