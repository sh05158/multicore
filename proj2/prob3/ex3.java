package proj2.prob3;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
  
public class ex3 {
  
    public static AtomicInteger a = new AtomicInteger(1);

    public static void main(String[] args){
        primeCalculator.a = a;
        primeCalculator.max = 100000;
        primeCalculator.primeCount.set(0);

         ArrayList<Thread> th = new ArrayList<Thread>();

         for(var i = 0; i<10; i++){
             Thread t = new Thread(new primeCalculator("Th"+i));
             th.add(t);
         }

         for(var i = 0; i<10; i++){
            th.get(i).start();
        }

        for(var i = 0; i<10; i++){
            try {
                th.get(i).join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println("prime count : "+primeCalculator.primeCount.get());
    }
}

class primeCalculator implements Runnable{
    String name;
    static AtomicInteger a;
    static AtomicInteger primeCount = new AtomicInteger(-1);
    static int max;

    primeCalculator(String name){
        this.name = name;
    }
    public void run(){
        while(true){
            int num = a.addAndGet(1);
            if(num < primeCalculator.max){
                if(isPrime(num)){
                    int count = primeCount.getAndAdd(1);
                }

            }
            else {
                break;
            }
        }
        
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