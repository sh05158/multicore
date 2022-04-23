import java.util.ArrayList;
import java.util.Arrays;

public class pc_static_cyclic {
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

    ArrayList<CyclicThread> thread_arr = new ArrayList<CyclicThread>();
    
    for(int i = 0; i<NUM_THREADS;i++){
      
      CyclicThread a = new CyclicThread();
      thread_arr.add(a);

    }


    int k = 0;

    while(k<=NUM_END){
      thread_arr.get(k%NUM_THREADS).addWork(k++);
    }
    
    for(int i = 0;i<thread_arr.size();i++){
      thread_arr.get(i).start();
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

class CyclicThread extends Thread {
  ArrayList<Integer> problem;
  int primeCount = 0;
  long startTime = System.currentTimeMillis();

  
  CyclicThread( ){
    this.problem = new ArrayList<Integer>();
  }

  public void addWork(int num){
    this.problem.add(num);
  }

  public void run(){
    System.out.println(this.getName()+" start! work size = "+this.problem.size());

    for(var i = 0; i<this.problem.size();i++){
      if(isPrime(this.problem.get(i))){
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