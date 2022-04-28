import java.util.*;
import java.lang.*;

// command-line execution example) java MatmultD 6 < mat500.txt
// 6 means the number of threads to use
// < mat500.txt means the file that contains two matrices is given as standard input
//
// In eclipse, set the argument value and file input by using the menu [Run]->[Run Configurations]->{[Arguments], [Common->Input File]}.

// Original JAVA source code: http://stackoverflow.com/questions/21547462/how-to-multiply-2-dimensional-arrays-matrix-multiplication
public class MatmultD
{
  private static Scanner sc = new Scanner(System.in);
  public static void main(String [] args)
  {
    int thread_no=0;
    if (args.length==1) thread_no = Integer.valueOf(args[0]);
    else thread_no = 2;
        
    int a[][]=readMatrix();
    int b[][]=readMatrix();

    long startTime = System.currentTimeMillis();

    ThreadforMatrix.a = a;
    ThreadforMatrix.b = b;
    ThreadforMatrix.ans = new int[a.length][a.length];

    ArrayList<ThreadforMatrix> thread_arr = new ArrayList<ThreadforMatrix>();
    
    for(int i = 0; i<thread_no;i++){
      int start = i*(a.length/thread_no);
      int end = i == thread_no-1 ? a.length : (i+1)*(a.length/thread_no);
      System.out.println("new thread range "+start+ " ~ "+end);
      ThreadforMatrix thread = new ThreadforMatrix(start,end, a.length);
      thread_arr.add(thread);
      thread.start();

    }

    for(int i = 0;i<thread_arr.size();i++){
      try {
        thread_arr.get(i).join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    long endTime = System.currentTimeMillis();

    System.out.printf("[thread_no]:%2d , [Time]:%4d ms\n", thread_no, endTime-startTime);

    printMatrix(ThreadforMatrix.ans);


  }

   public static int[][] readMatrix() {
       int rows = sc.nextInt();
       int cols = sc.nextInt();
       int[][] result = new int[rows][cols];
       for (int i = 0; i < rows; i++) {
           for (int j = 0; j < cols; j++) {
              result[i][j] = sc.nextInt();
           }
       }
       return result;
   }

  public static void printMatrix(int[][] mat) {
  System.out.println("Matrix["+mat.length+"]["+mat[0].length+"]");
    int rows = mat.length;
    int columns = mat[0].length;
    int sum = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        // System.out.printf("%4d " , mat[i][j]);
        sum+=mat[i][j];
      }
      // System.out.println();
    }
    // System.out.println();
    System.out.println("Matrix Sum = " + sum + "\n");
  }
}

class ThreadforMatrix extends Thread {
  static int a[][];
  static int b[][];

  static int ans[][];
  
  int start;
  int end;


  ThreadforMatrix(int start, int end, int size){
    this.start = start;
    this.end = end;
  }

  public void run(){
    long startTime = System.currentTimeMillis();

    int n = a[0].length;
    int m = a.length;
    int p = b[0].length;

    for(int i = this.start;i < this.end;i++){
      for(int j = 0;j < p;j++){
        for(int k = 0;k < n;k++){
          ans[i][j] += a[i][k] * b[k][j];
        }
      }
    }

    long endTime = System.currentTimeMillis();
    long timeDiff = endTime - startTime;

    System.out.println(this.getName()+" Execution Time: "+timeDiff+"ms");
  }

}