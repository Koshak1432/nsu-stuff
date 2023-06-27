#!/bin/bash

for POWER in $(seq 10); do
	export CHUNKS=$((2 ** $POWER))
        echo -n "$CHUNKS"
        for _ in $(seq 3); do
                export OMP_NUM_THREADS=6
                printf ",%f" $(./lab A.bin B.bin -one_section)
        done
        echo
done

