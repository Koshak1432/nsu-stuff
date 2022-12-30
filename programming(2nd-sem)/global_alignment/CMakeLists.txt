cmake_minimum_required(VERSION 3.20)
project(global_alignment C)

set(CMAKE_C_STANDARD 99)

add_executable(global_alignment main.c global_alignment.c global_alignment.h)