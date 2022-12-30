cmake_minimum_required(VERSION 3.20)
project(dijkstra C)

set(CMAKE_C_STANDARD 99)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fsanitize=address -fsanitize=leak -fsanitize=undefined -static-libasan -Wall -Wextra -Wpedantic -g")
add_executable(dijkstra main.c graph.c hash_table.c hash_table.h queue.c queue.h graph.h util.c util.h)
