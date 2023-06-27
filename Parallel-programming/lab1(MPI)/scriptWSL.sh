#!/bin/bash
#PBS -l walltime=00:59:50
#PBS -l select=2:ncpus=12:mem=2048m

for THREADS in 1 2 4 6 8 12; do 
	echo -n "$THREADS"
	MPI_NP=$THREADS
	for _ in $(seq 3); do
		printf ",%f" $(mpirun -np $MPI_NP ./prog A.bin B.bin)
	done
	echo
done

