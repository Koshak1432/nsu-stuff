#!/usr/bin/env bash

set -eu

for POWER in $(seq 16); do
	export BARRIER=$((2 ** $POWER))
	echo -n "$BARRIER"
	for _ in $(seq 5); do
		export OMP_NUM_THREADS=6
		printf ",%f" $(./main)
	done
	echo
done
