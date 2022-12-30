#!/usr/bin/env bash

set -eu

export BARRIER=131072
for THREADS in $(seq 16); do
	echo -n "$THREADS,"
	for _ in $(seq 5); do
		export OMP_NUM_THREADS=$THREADS
		printf ",%f" $(./main)
	done
	echo
done
										
