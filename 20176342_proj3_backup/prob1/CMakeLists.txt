cmake_minimum_required(VERSION 3.16)
project(prob1 C)
set(CMAKE_C_STANDARD 11)


find_package(OpenMP REQUIRED)

set (CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${OpenMP_C_FLAGS}")
set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${OpenMP_CXX_FLAGS}")

add_executable(prob1
        prob1.c)