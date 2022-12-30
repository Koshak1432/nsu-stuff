#!/bin/bash
#PBS -l walltime=00:40:00
#PBS -l select=2:ncpus=12:mem=3072m

for THREADS in 1 2 4 6 8 16; do 
	echo -n "$THREADS"
	MPI_NP=$THREADS
	for _ in $(seq 3); do
		printf ",%f" $(mpirun -np $MPI_NP ./prog $384)
	done
	echo
done

