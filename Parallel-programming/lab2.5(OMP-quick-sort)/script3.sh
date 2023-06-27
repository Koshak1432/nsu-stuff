#!/usr/bin/env bash

set -eu

export BARRIER=262144
	echo -n "262144,"
	for _ in $(seq 5); do
	export OMP_NUM_THREADS=6
	printf ",%f" $(./main)
	done
	echo
