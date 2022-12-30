#!/bin/bash
#PBS -l walltime=00:50:50
#PBS -l select=2:ncpus=12:mem=3072m

cd $PBS_O_WORKDIR

for THREADS in 1 2 4 6 8 16; do 
	echo -n "$THREADS"
	MPI_NP=$THREADS
	for _ in $(seq 3); do
		printf ",%f" $(mpirun -machinefile $PBS_NODEFILE -np $MPI_NP ./prog $384)
	done
	echo
done

