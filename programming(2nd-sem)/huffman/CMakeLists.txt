cmake_minimum_required(VERSION 3.20)
project(huffman C)

set(CMAKE_C_STANDARD 99)

add_executable(huffman main.c sort.c sort.h huffman_encode.c huffman_encode.h huffman_decode.c huffman_decode.h codes_table.c codes_table.h huffman.h huffman.c)