package proj2.prob3;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ex1 {

    public static void main(String[] args) {

        BlockingQueue storage = new ArrayBlockingQueue<String>(50);
        
        Thread factory = new Thread(new FoodFactory(storage));
        
        factory.start();

        for(var i = 0; i<3; i++){
            Thread monkey = new Thread(new Monkey("Monkey"+i, storage));
            monkey.start();
        }

        for(var i = 0; i<3; i++){
            Thread human = new Thread(new Human("Human"+i, storage));
            human.start();
        }
    }
}

class FoodFactory implements Runnable{
    
    private BlockingQueue storage;
    private String[] foodList;
    
    public FoodFactory(BlockingQueue storage) {
        this.storage = storage;
        this.foodList = new String[]{"Banana","Rice"};
    }

    @Override
    public void run() {
        
        for (int i = 0; i < 1000; i++) {

            int k = (int)(Math.random()*2);
            System.out.println(k);
            try {
                 System.out.println("Factory produce : "+this.foodList[k]);  
                 storage.put(this.foodList[k]);
                 System.out.println("Factory produce Done. Current :"+storage);
            
            
                 Thread.sleep((int)(Math.random()*4000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Monkey implements Runnable{
    
    private BlockingQueue storage;
    String name;
    public Monkey(String name, BlockingQueue storage) {
        this.name = name;
        this.storage = storage;
    }

    @Override
    public void run() {
        String str;
        
        for (int i = 0; i < 1000; i++) {
            try {
                System.out.println(this.name+ " : Trying to take Banana");  
                str=(String)storage.take();
                if(str.equals("Banana")){
                    System.out.println(this.name+" Takes Banana! Queue Size : "+storage.size());
                }
                else {
                    System.out.println(this.name+" Takes Rice. Put it back ");
                    storage.put(str);

                }
                Thread.sleep((int)(Math.random()*15000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Human implements Runnable{
    
    private BlockingQueue storage;
    String name;
    public Human(String name, BlockingQueue storage) {
        this.name = name;
        this.storage = storage;
    }

    @Override
    public void run() {
        String str;
        
        for (int i = 0; i < 1000; i++) {
            try {
                System.out.println(this.name+ " : Trying to Rice");  
                str=(String)storage.take();
                if(str.equals("Rice")){
                    System.out.println(this.name+" Takes Rice! Queue Size : "+storage.size());
                }
                else {
                    System.out.println(this.name+" Takes Banana. Put it back ");
                    storage.put(str);

                }
                Thread.sleep((int)(Math.random()*15000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}