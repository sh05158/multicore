#include <omp.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/time.h>
#include <time.h>
#include <stdint-gcc.h>

int isPrime(int);

int main (int argc, char** args)
{
    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC_RAW, &start);
//do stuff



    int num_threads;
    int num_end = 200000;

	int i;
    int primeCount = 0;
	omp_set_num_threads(atoi(args[2]));
    num_threads = atoi(args[2]);
    printf("program starts!\n");
    printf("Thread num = %d , Option = %d\n",atoi(args[2]),atoi(args[1]));

    int option = atoi(args[1]);
    switch(option){
        //static with default chunk size
        case 1:
            #pragma omp parallel for schedule(static) reduction (+:primeCount)
            for (i = 0; i < num_end; i++) {
//                int target = omp_get_thread_num()*(num_end/num_threads)+1;
                isPrime(i) == 1 && primeCount++;

//                printf("i=%d (%d/%d)   %d\n",i,omp_get_thread_num(),omp_get_num_threads(), target);
            }
            break;

        case 2:
            #pragma omp parallel for schedule(dynamic) reduction (+:primeCount)
            for (i = 0; i < num_end; i++) {
//                int target = omp_get_thread_num()*(num_end/num_threads)+1;
                isPrime(i) == 1 && primeCount++;

//                printf("i=%d (%d/%d)   %d\n",i,omp_get_thread_num(),omp_get_num_threads(), target);
            }
            break;
        case 3:
            #pragma omp parallel for schedule(static,10) reduction (+:primeCount)
            for (i = 0; i < num_end; i++) {
//                int target = omp_get_thread_num()*(num_end/num_threads)+1;
                isPrime(i) == 1 && primeCount++;

//                printf("i=%d (%d/%d)   %d\n",i,omp_get_thread_num(),omp_get_num_threads(), target);
            }
            break;
        case 4:
            #pragma omp parallel for schedule(dynamic,10) reduction (+:primeCount)
            for (i = 0; i < num_end; i++) {
//                int target = omp_get_thread_num()*(num_end/num_threads)+1;
                isPrime(i) == 1 && primeCount++;

//                printf("i=%d (%d/%d)   %d\n",i,omp_get_thread_num(),omp_get_num_threads(), target);
            }
            break;

    }

    clock_gettime(CLOCK_MONOTONIC_RAW, &end);

	printf("program ends! prime count = %d\n",primeCount);

    uint64_t delta_us = (end.tv_sec - start.tv_sec) * 1000000 + (end.tv_nsec - start.tv_nsec) / 1000;
    printf("execution time  %lu ms\n",delta_us/1000);

	return 1;
}

int isPrime(int x){
    int i;
    if(x<=1) return 0;
    for(i=2;i<x;i++){
        if(x%i == 0) return 0;
    }
    return 1;
}