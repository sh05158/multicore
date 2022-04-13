package proj1.problem1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class pc_dynamic {
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

    ArrayList<DynamicThread> thread_arr = new ArrayList<DynamicThread>();
    
    final Lock lock = new ReentrantLock();

    for(int i = 0; i<NUM_THREADS;i++){
      
      DynamicThread a = new DynamicThread(lock);
      thread_arr.add(a);

    }


    int k = 0;

    while(k<=NUM_END){
      // thread_arr.get(k%NUM_THREADS).addWork(k++);
      DynamicThread.addWork(k++);
    }
    System.out.println(DynamicThread.problem.size());
    
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

class DynamicThread extends Thread {
  static ArrayList<Integer> problem = new ArrayList<Integer>();
  int primeCount = 0;
  long startTime = System.currentTimeMillis();
  private Lock lock;

  
  DynamicThread( Lock lock ){
    this.lock = lock;
  }

  public static void addWork(int num){
    problem.add(num);
  }

  public synchronized void work(){
    int currWork = 0;
    while(true){
      currWork=getWork();
      if(currWork == -1){
        break;
      }
      if( isPrime(currWork) ){
        primeCount++;
      }
    }

  }

  public int getWork(){
    lock.lock();

    if(problem.size()>0){
        int val;
        val = problem.get(0);
        problem.remove(0);
        lock.unlock();

        return val;
    }
    else {
      System.out.println(this.getName()+" empty");
    }
    lock.unlock();

    return -1;
  }

  public void run(){
    System.out.println(this.getName()+" start! work size = "+problem.size());

    work();

    long endTime = System.currentTimeMillis();
    long timeDiff = endTime - startTime;

    System.out.println(this.getName()+" Execution Time: "+timeDiff+"ms");
  }

  public int getResult(){
    // System.out.println(this.getName()+ " calculated prime count= "+primeCount);
    return primeCount;
  }

  private boolean isPrime(int x){
    int i;
    if(x<=1) return false;
    for(i=2;i<x;i++){
      if(x%i == 0) return false;
    }
    return true;
  }
}