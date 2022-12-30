cmake_minimum_required(VERSION 3.17)
project(cache C)

set(CMAKE_C_STANDARD 11)

include_directories(.)

add_executable(cache
        hash_table.c
        hash_table.h
        list.c
        list.h
        main.c cache.c cache.h util.c util.h)
