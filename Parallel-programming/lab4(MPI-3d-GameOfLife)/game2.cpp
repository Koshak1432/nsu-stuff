#include <mpi.h>
#include <iostream>
#include <cstring>
#include <vector>
#include <fstream>
//#include <C:\Program Files (x86)\Microsoft MPI\MPI_SDK\Include\mpi.h>

using namespace std;

constexpr int rootRank = 0;
constexpr char LIVE_CELL = 'o';
constexpr char DEAD_CELL = 'x';
constexpr int lineSize = 112;
constexpr int areaSize = lineSize * lineSize;

int getToroidCoord(int i, int max) {
	while (i < 0)
	{
		i += max;
	}
	return i % max;
}

struct Block {
	Block(int xSize, int ySize, int zSize) : xSize_(xSize), ySize_(ySize), zSize_(zSize), data_(new char[xSize * ySize * zSize]),
		birthRules_(move(vector<bool>(27, false))), survivalRules_(move(vector<bool>(27, false))) {
		memset(data_, DEAD_CELL, xSize * ySize * zSize * sizeof(char));
		birthRules_[6] = true;
		survivalRules_[5] = true;
		survivalRules_[7] = true;
	};
	~Block() {
		delete[] data_;
	}

	bool getCell(int x, int y, int z) const {
		return LIVE_CELL == data_[xSize_ * ySize_ * getToroidCoord(z, zSize_) +
				xSize_ * getToroidCoord(y, ySize_) + getToroidCoord(x, xSize_)];
	}

	int countNeighbours(int x, int y, int z) const {
		int neighbours = 0;
		for (int i = z - 1; i <= z + 1; ++i) {
			for (int j = y - 1; j <= y + 1; ++j) {
				for (int k = x - 1; k <= x + 1; ++k) {
					if ((x == k) && (y == j) && (z == i)) {
						continue;
					}
					if (getCell(k, j, i)) {
						++neighbours;
					}
				}
			}
		}
		return neighbours;
	}

	int countNeighboursUpper(int x, int y, int z, Block const &upperBlock) const {
		int neighbours = 0;
		for (int i = z; i <= z + 1; ++i) {
			for (int j = y - 1; j <= y + 1; ++j) {
				for (int k = x - 1; k <= x + 1; ++k) {
					if ((x == k) && (y == j) && (z == i)) {
						continue;
					}
					if (getCell(k, j, i)) {
						++neighbours;
					}
				}
			}
		}

		for (int j = y - 1; j <= y + 1; ++j) {
			for (int k = x - 1; k <= x + 1; ++k) {
				if (upperBlock.getCell(k, j, 0)) {
					++neighbours;
				}
			}
		}

		return neighbours;
	}

	int countNeighboursLower(int x, int y, int z, Block const &lowerBlock) const {
		int neighbours = 0;
		for (int i = z - 1; i <= z; ++i) {
			for (int j = y - 1; j <= y + 1; ++j) {
				for (int k = x - 1; k <= x + 1; ++k) {
					if ((x == k) && (y == j) && (z == i)) {
						continue;
					}
					if (getCell(k, j, i)) {
						++neighbours;
					}
				}
			}
		}

		for (int j = y - 1; j <= y + 1; ++j) {
			for (int k = x - 1; k <= x + 1; ++k) {
				if (lowerBlock.getCell(k, j, 0)) {
					++neighbours;
				}
			}
		}

		return neighbours;
	}

	int xSize_;
	int ySize_;
	int zSize_;
	char *data_;
	vector<bool> birthRules_;
	vector<bool> survivalRules_;
};

void updateMiddle(Block const &currentBlock, Block &nextBlock) {
	for (int z = 1; z < nextBlock.zSize_ - 1; ++z) {
		for (int y = 0; y < nextBlock.ySize_; ++y) {
			for (int x = 0; x < nextBlock.xSize_; ++x) {
				int idx = z * currentBlock.xSize_ * currentBlock.ySize_ + y * currentBlock.xSize_ + x;
				int neighbours = currentBlock.countNeighbours(x, y, z);
				bool cell = currentBlock.getCell(x, y, z);
				if (!cell) {
					nextBlock.data_[idx] = (currentBlock.birthRules_[neighbours]) ? LIVE_CELL : DEAD_CELL;
				} else {
					nextBlock.data_[idx] = (currentBlock.survivalRules_[neighbours]) ? LIVE_CELL : DEAD_CELL;
				}
			}
		}
	}
}

void updateUpper(Block const &currentBlock, Block &nextBlock, Block const &upperBlock) {
	int z = 0;
	for (int y = 0; y < nextBlock.ySize_; ++y) {
		for (int x = 0; x < nextBlock.xSize_; ++x) {
			int idx = z * currentBlock.xSize_ * currentBlock.ySize_ + y * currentBlock.xSize_ + x;
			int neighbours = currentBlock.countNeighboursUpper(x, y, z, upperBlock);
			bool cell = currentBlock.getCell(x, y, z);
			if (!cell) {
				nextBlock.data_[idx] = (currentBlock.birthRules_[neighbours]) ? LIVE_CELL : DEAD_CELL;
			} else {
				nextBlock.data_[idx] = (currentBlock.survivalRules_[neighbours]) ? LIVE_CELL : DEAD_CELL;
			}
		}
	}
}

void updateLower(Block const &currentBlock, Block &nextBlock, Block const &lowerBlock) {
	int z = currentBlock.zSize_ - 1;
	for (int y = 0; y < nextBlock.ySize_; ++y) {
		for (int x = 0; x < nextBlock.xSize_; ++x) {
			int idx = z * currentBlock.xSize_ * currentBlock.ySize_ + y * currentBlock.xSize_ + x;
			int neighbours = currentBlock.countNeighboursLower(x, y, z, lowerBlock);
			bool cell = currentBlock.getCell(x, y, z);
			if (!cell) {
				nextBlock.data_[idx] = (currentBlock.birthRules_[neighbours]) ? LIVE_CELL : DEAD_CELL;
			} else {
				nextBlock.data_[idx] = (currentBlock.survivalRules_[neighbours]) ? LIVE_CELL : DEAD_CELL;
			}
		}
	}
}

void updateState(Block &currentBlock, Block &nextBlock, Block &upperBlock, Block &lowerBlock, int lowerNeighbour, int upperNeighbour) {
	MPI_Request sendFirst, sendLast, recFirst, recLast;

	MPI_Isend(currentBlock.data_, areaSize, MPI_CHAR, upperNeighbour, 0, MPI_COMM_WORLD, &sendFirst);
	MPI_Isend(currentBlock.data_ + (currentBlock.zSize_ - 1) * areaSize, areaSize, MPI_CHAR, lowerNeighbour, 0, MPI_COMM_WORLD, &sendLast);
	MPI_Irecv(upperBlock.data_, areaSize, MPI_CHAR, upperNeighbour, 0, MPI_COMM_WORLD, &recFirst);
	MPI_Irecv(lowerBlock.data_, areaSize, MPI_CHAR, lowerNeighbour, 0, MPI_COMM_WORLD, &recLast);

	updateMiddle(currentBlock, nextBlock);

	int flagFirst = false;
	int flagLast = false;
	while (!flagFirst || !flagLast) {
		if (!flagFirst) {
			MPI_Test(&recFirst, &flagFirst, MPI_STATUS_IGNORE);
			if (flagFirst) {
				updateUpper(currentBlock, nextBlock, upperBlock);
			}
		}
		if (!flagLast) {
			MPI_Test(&recLast, &flagLast, MPI_STATUS_IGNORE);
			if (flagLast) {
				updateLower(currentBlock, nextBlock, lowerBlock);
			}
		}
	}
}

void fillBlock(Block &block) {
	//glider period 4
	block.data_[2 * areaSize + 5 * lineSize + 6] = LIVE_CELL;
	block.data_[2 * areaSize + 5 * lineSize + 5] = LIVE_CELL;
	block.data_[2 * areaSize + 6 * lineSize + 5] = LIVE_CELL;
	block.data_[2 * areaSize + 6 * lineSize + 6] = LIVE_CELL;
	block.data_[2 * areaSize + 7 * lineSize + 5] = LIVE_CELL;
	block.data_[2 * areaSize + 7 * lineSize + 6] = LIVE_CELL;

	block.data_[3 * areaSize + 7 * lineSize + 5] = LIVE_CELL;
	block.data_[3 * areaSize + 7 * lineSize + 6] = LIVE_CELL;

	block.data_[4 * areaSize + 6 * lineSize + 5] = LIVE_CELL;
	block.data_[4 * areaSize + 6 * lineSize + 6] = LIVE_CELL;
}

void writeCoordsOfLiveCells(Block const &block, string name) {
	std::ofstream out(name, std::ios::out | std::ios::binary | std::ios::trunc);
	if (out.fail()) {
		std::cerr << "Couldn't open output file" << std::endl;
		return;
	}

	for (int z = 0; z < block.zSize_; ++z) {
		for (int y = 0; y < block.ySize_; ++y) {
			for (int x = 0; x < block.xSize_; ++x) {
				if (block.getCell(x, y, z)) {
					out << "(" << x << ", " << y << ", " << z << ")" << endl;
				}
			}
		}
	}
	out.close();
}

int main(int argc, char **argv)
{
	MPI_Init(&argc, &argv);
	int numProcesses, rank, numIter, lowerNeighbour, upperNeighbour;
	double start, end;

	MPI_Comm_size(MPI_COMM_WORLD, &numProcesses);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);

	numIter = 4 * lineSize;

	int *sizes = new int[numProcesses];
	int *displs = new int[numProcesses];
	{
		int remain = lineSize % numProcesses;
		int base = lineSize / numProcesses;
		int offset = 0;
		for (int i = 0; i < numProcesses; ++i) {
			displs[i] = offset;
			sizes[i] = base * areaSize;
			offset += base * areaSize;
			if (remain) {
				sizes[i] += areaSize;
				offset += areaSize;
				--remain;
			}
		}
	}

	Block allSpace(lineSize, lineSize, lineSize);
	Block currentBlock(lineSize, lineSize, sizes[rank] / areaSize);
	Block nextBlock(lineSize, lineSize, sizes[rank] / areaSize);
	Block upperBlock(lineSize, lineSize, 1);
	Block lowerBlock(lineSize, lineSize, 1);

	if (rootRank == rank) {
		fillBlock(allSpace);
//		writeCoordsOfLiveCells(allSpace, "initCoords");
		start = MPI_Wtime();
	}

	MPI_Scatterv(allSpace.data_, sizes, displs, MPI_CHAR, currentBlock.data_, sizes[rank], MPI_CHAR, 0, MPI_COMM_WORLD);
	lowerNeighbour = (rank + 1) % numProcesses;
	upperNeighbour = (rank - 1 >= 0) ? rank - 1 : numProcesses - 1;

	for (int i = 0; i < numIter; ++i) {
		updateState(currentBlock, nextBlock, upperBlock, lowerBlock, lowerNeighbour, upperNeighbour);
		swap(currentBlock.data_, nextBlock.data_);
	}

	MPI_Gatherv(currentBlock.data_, sizes[rank], MPI_CHAR, allSpace.data_, sizes, displs, MPI_CHAR, 0, MPI_COMM_WORLD);

	if (rootRank == rank) {
		end = MPI_Wtime();
		cout << end - start << endl;
//		writeCoordsOfLiveCells(allSpace, "outCoords");
	}
	delete[] sizes;
	delete[] displs;

	MPI_Finalize();
	return 0;
}
