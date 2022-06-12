//
// Created by song on 2022-05-30.
//

#include <omp.h>
#include <stdio.h>
#include <stdlib.h>
//#include <sys/time.h>
//#include <windows.h>
#include <time.h>
#include <math.h>

#define CUDA 0
#define OPENMP 1
#define SPHERES 20

#define rnd( x ) (x * rand() / RAND_MAX)
#define INF 2e10f
#define DIM 2048
void ppm_writes();

void kernel();

struct Sphere {
    //define Sphere ( there is no hit function in sphere)
    float   r,b,g;
    float   radius;
    float   x,y,z;

};

void ppm_writes(unsigned char* bitmap, int xdim,int ydim, FILE* fp)
{
    //ppm write function 
    int i,x,y;
    fprintf(fp,"P3\n");
    fprintf(fp,"%d %d\n",xdim, ydim);
    fprintf(fp,"255\n");


    for (y=0;y<ydim;y++) {
        for (x=0;x<xdim;x++) {
            i=x+y*xdim;
            fprintf(fp,"%d %d %d ",bitmap[4*i],bitmap[4*i+1],bitmap[4*i+2]);
        }
        fprintf(fp,"\n");
    }
}

int isPrime(int);

int main (int argc, char** args){

//    unsigned long startTime = timeGetTime();
    clock_t start, end;

    start = clock(); //define start time 


    int num_threads;


    omp_set_num_threads(atoi(args[1])); //parse num of thread parameter
    num_threads = atoi(args[1]);

    int x,y;
    unsigned char* bitmap;


    FILE* fp = fopen("result.ppm","w"); //write result.ppm file 


    struct Sphere *temp_s = (struct Sphere*)malloc( sizeof(struct Sphere) * SPHERES ); //define spheres array
    for (int i=0; i<SPHERES; i++) {
        temp_s[i].r = rnd( 1.0f );
        temp_s[i].g = rnd( 1.0f );
        temp_s[i].b = rnd( 1.0f );
        temp_s[i].x = rnd( 2000.0f ) - 1000;
        temp_s[i].y = rnd( 2000.0f ) - 1000;
        temp_s[i].z = rnd( 2000.0f ) - 1000;
        temp_s[i].radius = rnd( 200.0f ) + 40;
    }

    bitmap=(unsigned char*)malloc(sizeof(unsigned char)*DIM*DIM*4); //define bitmap



#pragma omp parallel for schedule(guided) collapse(2) // run kernel function with omp parallel (collapse(2)) nested loop
    for (x = 0; x < DIM; x++){
        for (y = 0; y < DIM; y++) {
            kernel(x, y, temp_s, bitmap);
        }
    }

    end = clock();

    printf("OpenMP (%d threads) ray tracing: %lf sec\n",num_threads,(double)(end - start)/1000.0);

    ppm_writes(bitmap, DIM, DIM, fp); //ppm write 

    fclose(fp);
    free(bitmap);
    free(temp_s);



    printf("[result.ppm] was generated.");

}

float hit(struct Sphere s, float ox, float oy, float *n){
    //hit function
    float dx = ox - s.x;
    float dy = oy - s.y;
    if (dx*dx + dy*dy < s.radius*s.radius) {
        float dz = sqrtf( s.radius*s.radius - dx*dx - dy*dy );
        *n = dz / sqrtf( s.radius * s.radius );
        return dz + s.z;
    }
    return -INF;
}

void kernel(int x, int y, struct Sphere* s, unsigned char* ptr)
{
    //kernel function to point a pixel which has circle area 
    int offset = x + y*DIM;
    float ox = (x - DIM/2);
    float oy = (y - DIM/2);


    float r=0, g=0, b=0;
    float   maxz = -INF;


    for(int i=0; i<SPHERES; i++) {
        //check all spheres
        float   n;
        float   t = hit( s[i],ox, oy, &n );
        if (t > maxz) {
            float fscale = n;
            r = s[i].r * fscale;
            g = s[i].g * fscale;
            b = s[i].b * fscale;
            maxz = t;
        }
    }

    ptr[offset*4 + 0] = (int)(r * 255);
    ptr[offset*4 + 1] = (int)(g * 255);
    ptr[offset*4 + 2] = (int)(b * 255);
    ptr[offset*4 + 3] = 255;
}
