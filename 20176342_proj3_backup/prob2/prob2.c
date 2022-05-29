#include <omp.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/time.h>
#include <time.h>
#include <stdint-gcc.h>

int isPrime(int);

long num_steps = 10000000;
//double step;

int main (int argc, char** args)
{

    struct timespec start2, end2;
    struct timespec start, end;
//do stuff

    clock_gettime(CLOCK_MONOTONIC_RAW, &start2);


    int num_threads;
    double step = 1.0/(double) num_steps;

	omp_set_num_threads(atoi(args[3]));
    num_threads = atoi(args[3]);
    long i; double x, pi,sum = 0;

    printf("program starts!\n");
    printf("schedule = %d , chunk_size = %d, num_threads = %d \n",atoi(args[1]),atoi(args[2]),atoi(args[3]));


    int option = atoi(args[1]);
    int chunkSize = atoi(args[2]);





    switch(option){
        //static with default chunk size
        case 1:

#pragma omp parallel for schedule(static, chunkSize) private(x) reduction (+:sum)
            for (i = 0; i < num_steps; i++) {
                x = (i + 0.5) * step;
                sum +=  4.0 / (1.0 + x * x);
            }



            break;

        case 2:

#pragma omp parallel for schedule(dynamic, chunkSize) private(x) reduction (+:sum)
            for (i = 0; i < num_steps; i++) {
                x = (i + 0.5) * step;
                sum +=  4.0 / (1.0 + x * x);
            }



            break;
        case 3:

#pragma omp parallel for schedule(guided, chunkSize) private(x) reduction (+:sum)
            for (i = 0; i < num_steps; i++) {
                x = (i + 0.5) * step;
                sum += 4.0 / (1.0 + x * x);
            }




            break;

    }


    pi = sum*step;


    printf("pi=%.24lf\n",pi);

    clock_gettime(CLOCK_MONOTONIC_RAW, &end2);

    uint64_t delta_us = (end2.tv_sec - start2.tv_sec) * 1000000 + (end2.tv_nsec - start2.tv_nsec) / 1000;
    printf("execution time  %lu ms\n",delta_us/1000);

	return 1;
}