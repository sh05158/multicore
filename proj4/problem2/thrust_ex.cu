
#include "cuda_runtime.h"
#include "device_launch_parameters.h"

#include <stdio.h>

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>

#include <omp.h>
#include <cuda.h>

#include <thrust/host_vector.h>
#include <thrust/device_vector.h>
#include <thrust/transform.h>
#include <thrust/transform_reduce.h>

#define CUDA 0
#define OPENMP 1

#define rnd( x ) (x * rand() / RAND_MAX)
#define INF 2e10f

#define STEPS 1000000000
#define STEP 1/STEPS


struct saxpy_functor // define functor to calculate pie
{

	__host__ __device__
		double operator()(const int& x) const {
		double temp = (x + 0.5)*STEP;

		return (4.0 / (1.0 + temp * temp));
	}
};

int main()
{
	clock_t start, end;


	start = clock();
	double x, pi, sum = 0.0;
	
	thrust::counting_iterator<int> a(0); //define counting iterator 
	double result = thrust::transform_reduce(a,a+STEPS,saxpy_functor(), 0.0, thrust::plus<double>()); // reduce all result (add) with functor 

	pi = result * STEP; // calculate pi
	printf("pi=%.8lf\n", pi);

	end = clock();//measure program execution time 
 
	printf("PI Calculation: %lf sec   with step size = %d \n", (double)(end - start) / 1000.0,STEPS );

}
