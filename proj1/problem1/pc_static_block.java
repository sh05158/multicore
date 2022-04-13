package proj1.problem1;

import java.util.ArrayList;
import java.util.Arrays;

public class pc_static_block {
  private static int NUM_END = 200000;
  private static int NUM_THREADS = 1;
  public static void main (String[] args){
    if(args.length==2){
      NUM_THREADS = Integer.parseInt(args[0]);
      NUM_END = Integer.parseInt(args[1]);
    }

    int[] problem = new int[NUM_END];
    for(int i = 0; i<NUM_END;i++){
      problem[i] = i;
    }

    int counter = 0;
    long startTime = System.currentTimeMillis();

    ArrayList<BlockThread> thread_arr = new ArrayList<BlockThread>();
    
    for(int i = 0; i<NUM_THREADS;i++){
      int start = i*(NUM_END/NUM_THREADS)+1;
      int end = i == NUM_THREADS-1 ? NUM_END : (i+1)*(NUM_END/NUM_THREADS);
      System.out.println("new thread range "+start+ " ~ "+end);
      BlockThread a = new BlockThread(Arrays.copyOfRange(problem, start , end));
      thread_arr.add(a);
      a.start();

    }

    for(int i = 0;i<thread_arr.size();i++){
      try {
        thread_arr.get(i).join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    for(int i = 0;i<thread_arr.size();i++){
      counter += thread_arr.get(i).getResult();
    }

    long endTime = System.currentTimeMillis();
    long timeDiff = endTime - startTime;

    System.out.println("Program Execution Time: "+timeDiff+"ms");
    System.out.println("1..."+(NUM_END-1)+" prime# counter=" + counter);
  }

}

class BlockThread extends Thread {
  int[] problem;
  int primeCount = 0;
  long startTime = System.currentTimeMillis();

  
  BlockThread( int[] problem ){
    this.problem = problem;
  }

  public void run(){
    System.out.println(this.getName()+" start!");

    for(var i = 0; i<this.problem.length;i++){
      if(isPrime(this.problem[i])){
        primeCount++;
      }
    }

    long endTime = System.currentTimeMillis();
    long timeDiff = endTime - startTime;

    System.out.println(this.getName()+" Execution Time: "+timeDiff+"ms");
  }

  public int getResult(){
    return this.primeCount;
  }

  private static boolean isPrime(int x){
    int i;
    if(x<=1) return false;
    for(i=2;i<x;i++){
      if(x%i == 0) return false;
    }
    return true;
  }
}