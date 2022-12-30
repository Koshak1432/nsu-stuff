#!/usr/bin/env bash

for THREADS in $(seq 6); do
        echo -n "$THREADS"
        for _ in $(seq 3); do
		 export OMP_NUM_THREADS=$THREADS
                 printf ",%f" $(./lab A.bin B.bin -one_section)
        done
 	echo
done
