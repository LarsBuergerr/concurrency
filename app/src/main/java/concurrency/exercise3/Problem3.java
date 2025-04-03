package concurrency.exercise3;

import java.util.Date;

public class Problem3 {
  
  public static void main(String[] args) throws InterruptedException {
    int executionTimes = 100000;

    Synchronized synchronizedThread = new Synchronized(executionTimes);
    Unsynchronized unsynchronizedThread = new Unsynchronized(executionTimes);

    Date startTimeSynchronized = new Date();
    synchronizedThread.start();
    synchronizedThread.join();
    Date endTimeSynchronized = new Date();

    
    Date startTimeUnsynchronized = new Date();
    unsynchronizedThread.start();
    unsynchronizedThread.join();
    Date endTimeUnsynchronized = new Date();

    long synchronizedTime = endTimeSynchronized.getTime() - startTimeSynchronized.getTime();
    long unsynchronizedTime = endTimeUnsynchronized.getTime() - startTimeUnsynchronized.getTime();
    double percentDifference = ((double) (synchronizedTime - unsynchronizedTime) / unsynchronizedTime) * 100;

    System.out.println("Synchronized thread execution time: " + (endTimeSynchronized.getTime() - startTimeSynchronized.getTime()) + " ms");
    System.out.println("Unsynchronized thread execution time: " + (endTimeUnsynchronized.getTime() - startTimeUnsynchronized.getTime()) + " ms");
    System.out.println("Percentile difference: " + percentDifference + "%");
  }
}



class Synchronized extends Thread {

  int executionTimes;

  public Synchronized(int executionTimes) {
    this.executionTimes = executionTimes;
  }

  @Override
  public void run() {
    synchronized (this) {
      System.out.println("Thread " + Thread.currentThread().getName() + " is running: ");
      for (int i = 0; i < this.executionTimes; i++) {
        System.out.println(i);
      }
    }
  }
}

class Unsynchronized extends Thread {

  int executionTimes;

  public Unsynchronized(int executionTimes) {
    this.executionTimes = executionTimes;
  }

  @Override
  public void run() {
    System.out.println("Thread " + Thread.currentThread().getName() + " is running: ");
    for (int i = 0; i < this.executionTimes; i++) {
      System.out.println(i);
    }
  }


}