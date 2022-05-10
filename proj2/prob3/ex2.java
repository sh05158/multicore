package proj2.prob3;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
  
public class ex2 {
  
    private static final ReentrantReadWriteLock lock
        = new ReentrantReadWriteLock(true);
    private static String message = "";
  
    public static void main(String[] args)
        throws InterruptedException
    {
  
        ArrayList<Thread> readThreads = new ArrayList<Thread>();

        for(var i = 0; i<3; i++){
            Thread t = new Thread(new Read("Read"+i));
            readThreads.add(t);
        }
        Thread t2 = new Thread(new Writer1());
        Thread t3 = new Thread(new Writer2());
        Thread t4 = new Thread(new Writer3());
  
        for(var i = 0; i<3; i++){
            readThreads.get(i).start();
        }
        t2.start();
        t3.start();
        t4.start();
        for(var i = 0; i<3; i++){
            readThreads.get(i).join();
        }
        t2.join();
        t3.join();
        t4.join();

    }
  
    static class Read implements Runnable {
        String name;
        Read(String name){
            this.name = name;
        }
        public void run()
        {
  
            for (int i = 0; i <= 10; i++) {
                lock.readLock().lock();
  
                System.out.println( this.name + " : Message = " + message);
                lock.readLock().unlock();
            }
        }
    }

    static class Writer1 implements Runnable {
        public void run()
        {
  
            for (int i = 0; i <= 10; i++) {
                try {
                    lock.writeLock().lock();
                    System.out.println( "Writer 1 Will concat 1 = " + message);

                    message = message.concat("1");
                }
                finally {
                    lock.writeLock().unlock();
                }
            }
        }
    }

    static class Writer2 implements Runnable {
        public void run()
        {
  
            for (int i = 0; i <= 10; i++) {
                try {
                    lock.writeLock().lock();
                    System.out.println( "Writer 2 Will concat 2 " + message);

                    message = message.concat("2");
                }
                finally {
                    lock.writeLock().unlock();
                }
            }
        }
    }

    static class Writer3 implements Runnable {
        public void run()
        {
  
            for (int i = 0; i <= 10; i++) {
                try {
                    lock.writeLock().lock();
                    System.out.println( "Writer 3 Will concat 3 " + message);

                    message = message.concat("3");
                }
                finally {
                    lock.writeLock().unlock();
                }
            }
        }
    }
  
}