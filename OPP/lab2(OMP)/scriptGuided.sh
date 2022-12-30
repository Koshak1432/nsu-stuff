#!/bin/bash

for _ in $(seq 5); do
	export OMP_NUM_THREADS=6
	printf "%f," $(./lab A.bin B.bin -one_section)
done
