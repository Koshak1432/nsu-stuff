cmake_minimum_required(VERSION 3.17)
project(bloom C)

set(CMAKE_C_STANDARD 11)

include_directories(.)

add_executable(bloom
        main.c)
