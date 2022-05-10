package proj2.prob3;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
  
public class ex4 {
	private static CyclicBarrier  cyclicBarrier = new CyclicBarrier(10); 

	
	public static void main(String[] args) {
		for(int i = 0; i < 10; ++i) {
			new Thread(new Student(i)).start();
		}
	}

    public static class Student implements Runnable {
		private int id = 0;
        private double successRate = 30;
        private int tryCount = 0;
		private static Random random = new Random(System.currentTimeMillis());
		
		public Student(int id) {
			this.id = id;
		}
	
		@Override
		public void run() {
            while(true){
                int sleep = random.nextInt(2000) + 1000;
                try {
                    Thread.sleep(sleep);
                    boolean temp = this.tryToSubmit();
                    System.out.println("Student(" + id + ") Try("+(++tryCount)+") to submit homework = "+temp);
                    if(temp){
                        System.out.println("Student(" + id + ") Try("+(++tryCount)+") submitted homework Successfully");

                        try {
                            cyclicBarrier.await();
                        } catch (BrokenBarrierException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                
            }
			
			System.out.println("Profressor says All Student has submitted the homework : Student(" + id + ") END!");
		}

        private boolean tryToSubmit(){
            int a = (int)(Math.random()*100);

            if(a<successRate){
                return true;
            }
            else {
                return false;
            }
        }

	}
}