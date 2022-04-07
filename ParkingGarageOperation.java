class ParkingGarage {
    private int places;
    public ParkingGarage(int places) {
      if (places < 0)
        places = 0;
      this.places = places;
    }
    public synchronized void enter() { // enter parking garage
      while (places == 0) {
        try {
          wait();
        } catch (InterruptedException e) {}
      }
      places--;
    }
    public synchronized void leave() { // leave parking garage
      places++;
      notify();
    }
    public synchronized int getPlaces()
    {
      return places;
    }
  }
  
  
  class Car extends Thread {
    private ParkingGarage parkingGarage;
    public Car(String name, ParkingGarage p) {
      super(name);
      this.parkingGarage = p;
      start();
    }
  
    private void tryingEnter()
    {
        System.out.println(getName()+": trying to enter, current places="+parkingGarage.getPlaces());
    }
  
  
    private void justEntered()
    {
        System.out.println(getName()+": just entered, current places="+parkingGarage.getPlaces());
  
    }
  
    private void aboutToLeave()
    {
        System.out.println(getName()+":                                     about to leave, current places="+parkingGarage.getPlaces());
    }
  
    private void Left()
    {
        System.out.println(getName()+":                                     have been left, current places="+parkingGarage.getPlaces());
    }
  
    public void run() {
      while (true) {
        try {
          sleep((int)(Math.random() * 10000)); // drive before parking
        } catch (InterruptedException e) {}
        tryingEnter();
        parkingGarage.enter();
        justEntered();
        try {
          sleep((int)(Math.random() * 20000)); // stay within the parking garage
        } catch (InterruptedException e) {}
        aboutToLeave();
        parkingGarage.leave();
        Left();
  
      }
    }
  }
  
  
  public class ParkingGarageOperation {
    public static void main(String[] args){
      ParkingGarage parkingGarage = new ParkingGarage(30);
      for (int i=1; i<= 40; i++) {
        Car c = new Car("Car "+i, parkingGarage);
      }
    }
  }
  