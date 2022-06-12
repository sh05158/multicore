
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

#define CUDA 0
#define OPENMP 1
#define SPHERES 20

#define rnd( x ) (x * rand() / RAND_MAX)
#define INF 2e10f
#define DIM 2048

#define GRID_SIZE 128
#define BLOCK_SIZE 16

struct Sphere {
	//define Sphere with hit function
	float   r, b, g;
	float   radius;
	float   x, y, z;
	__device__ float hit(float ox, float oy, float *n) {
		float dx = ox - x;
		float dy = oy - y;
		if (dx*dx + dy * dy < radius*radius) {
			float dz = sqrtf(radius*radius - dx * dx - dy * dy);
			*n = dz / sqrtf(radius * radius);
			return dz + z;
		}
		return -INF;
	}
};

__global__ void kernel(unsigned char *c, Sphere* s)
{
	int x = blockIdx.x*blockDim.x + threadIdx.x; //define x axis
	int y = blockIdx.y*blockDim.y + threadIdx.y; //define y axis

	int offset = x + y * DIM;
	float ox = (x - DIM / 2);
	float oy = (y - DIM / 2);

	float r = 0, g = 0, b = 0;
	float   maxz = -INF;
	for (int i = 0; i < SPHERES; i++) { //find all Spheres to print a pixel
		float   n;
		float   t = s[i].hit(ox, oy, &n);
		if (t > maxz) {
			float fscale = n;
			r = s[i].r * fscale;
			g = s[i].g * fscale;
			b = s[i].b * fscale;
			maxz = t;
		}
	}

	c[offset * 4 + 0] = (int)(r * 255);
	c[offset * 4 + 1] = (int)(g * 255);
	c[offset * 4 + 2] = (int)(b * 255);
	c[offset * 4 + 3] = 255;

}

void ppm_write(unsigned char* bitmap, int xdim, int ydim, FILE* fp)
{
	//ppm write function to write result.ppm file
	int i, x, y;
	fprintf(fp, "P3\n");
	fprintf(fp, "%d %d\n", xdim, ydim);
	fprintf(fp, "255\n");
	for (y = 0; y < ydim; y++) {
		for (x = 0; x < xdim; x++) {
			i = x + y * xdim;
			fprintf(fp, "%d %d %d ", bitmap[4 * i], bitmap[4 * i + 1], bitmap[4 * i + 2]);
		}
		fprintf(fp, "\n");
	}
}

cudaError_t cudaRun(); // cudaRun function to help run cuda function


int main()
{

	cudaError_t cudaStatus = cudaRun(); // cuda Run
	if (cudaStatus != cudaSuccess)
	{
		fprintf(stderr, "cudaRun failed!");
		return -1;
	}

	cudaStatus = cudaDeviceReset();
	if (cudaStatus != cudaSuccess)
	{
		fprintf(stderr, "cudaRun failed!");
		return 1;
	}
	   
	return 0;
}

cudaError_t cudaRun()
{
	FILE *fp = fopen("result.ppm", "w"); //write empty ppm file 

	Sphere* dev_s = 0;

	unsigned char *dev_bitmap;
	unsigned char *bitmap;

	bitmap = (unsigned char*)malloc(sizeof(unsigned char)*DIM*DIM * 4); // allocate memory to host

	cudaError_t cudaStatus;

	Sphere *temp_s = (Sphere *) malloc(sizeof(Sphere) * SPHERES); //define random sphere array

	for (int i = 0; i < SPHERES; i++) {
        temp_s[i].r = rnd(1.0f);
        temp_s[i].g = rnd(1.0f);
        temp_s[i].b = rnd(1.0f);
        temp_s[i].x = rnd(2000.0f) - 1000;
        temp_s[i].y = rnd(2000.0f) - 1000;
        temp_s[i].z = rnd(2000.0f) - 1000;
        temp_s[i].radius = rnd(200.0f) + 40;
    }

	cudaStatus = cudaSetDevice(0); //set cuda device
	if (cudaStatus != cudaSuccess)
	{
		fprintf(stderr, "CudaSetDevice failed! Do you have a CUDA-capable GPU installed?");
		goto Error;
	}

	clock_t start, end;


	start = clock();

	cudaStatus = cudaMalloc((void**)&dev_s, SPHERES * sizeof(Sphere)); // memory allocate for gpu(device) with spheres
	if (cudaStatus != cudaSuccess)
	{
		fprintf(stderr, "cudaMalloc failed!");
		goto Error;
	}

	cudaStatus = cudaMalloc((void**)&dev_bitmap, sizeof(unsigned char) * DIM * DIM * 4); // memory allocate for gpu(device) with bitmap
	if (cudaStatus != cudaSuccess)
	{
		fprintf(stderr, "cudaMalloc failed!");
		goto Error;
	}

	cudaStatus = cudaMemcpy(dev_s, temp_s, SPHERES * sizeof(Sphere), cudaMemcpyHostToDevice); // copy variable from host to device (spheres)
	if (cudaStatus != cudaSuccess)
	{
		fprintf(stderr, "cudaMemcpy failed!");
		goto Error;
	}
	cudaStatus = cudaMemcpy(dev_bitmap, bitmap, sizeof(unsigned char) * DIM * DIM * 4, cudaMemcpyHostToDevice);  // copy variable from host to device (bitmap) maybe empty bitmap
	if (cudaStatus != cudaSuccess)
	{
		fprintf(stderr, "cudaMemcpy failed!");
		goto Error;
	}

	dim3 dimGrid(GRID_SIZE, GRID_SIZE, 1); // define grid 
	dim3 dimBlock(BLOCK_SIZE, BLOCK_SIZE, 1); //define block

	kernel<<<dimGrid, dimBlock>>>(dev_bitmap, dev_s); // run kernel function 

	cudaStatus = cudaGetLastError();
	if (cudaStatus != cudaSuccess)
	{
		fprintf(stderr, "cudaRun launch failed : %s\n", cudaGetErrorString(cudaStatus));
		goto Error;
	}

	cudaStatus = cudaDeviceSynchronize();
	if (cudaStatus != cudaSuccess)
	{
		fprintf(stderr, "cudaDeviceSynchronize returned error code %d after launching addKernel!\n", cudaStatus);
		goto Error;
	}

	cudaStatus = cudaMemcpy(bitmap, dev_bitmap,DIM*DIM * sizeof(unsigned char)*4, cudaMemcpyDeviceToHost); // copy result device to host
	if (cudaStatus != cudaSuccess)
	{
		fprintf(stderr, "cudaMemcpy failed!");
		goto Error;
	}

	end = clock(); // measure cuda program run time 

	printf("CUDA ray tracing: %lf sec\n", (double)(end - start) / 1000.0);

	ppm_write(bitmap,DIM,DIM,fp); // ppm write 
	printf("[result.ppm] was generated.\n");

    fclose(fp);


Error:
	//free memory allocate to gpu memory
	cudaFree(dev_s);
	cudaFree(dev_bitmap);

	return cudaStatus;
}

