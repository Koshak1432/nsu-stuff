cmake_minimum_required(VERSION 3.19)
project(eulerian_circuit C)

set(CMAKE_C_STANDARD 99)

add_executable(eulerian_circuit main.c graph.c graph.h)