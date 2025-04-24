package concurrency.exercise5;

public class Problem2 {
  public static void main(String[] args) throws InterruptedException {

    Object table = new Object();
    SynchronizedPhilosopher[] philosophers = new SynchronizedPhilosopher[5];
    for (int i = 0; i < philosophers.length; i++) {
      philosophers[i] = new SynchronizedPhilosopher((char) ('A' + i), table);
    }
    for (int i = 0; i < philosophers.length; i++) {
      philosophers[i].setLeft(philosophers[(i + philosophers.length - 1) % philosophers.length]);
      philosophers[i].setRight(philosophers[(i + 1) % philosophers.length]);
    }
    for (SynchronizedPhilosopher philosopher : philosophers) {
      philosopher.start();
    }
    Thread.sleep(10000);
    for (SynchronizedPhilosopher philosopher : philosophers) {
      philosopher.interrupt();
    }
    for (SynchronizedPhilosopher philosopher : philosophers) {
      philosopher.join();
    }
    System.out.println("All philosophers have left the table");
    System.out.println("The table is empty");
    System.out.println("The philosophers are still hungry");
  }
}

class SynchronizedPhilosopher extends Thread {
  private final char id;
  private boolean eating;
  private SynchronizedPhilosopher left, right;
  private final Object table;

  public SynchronizedPhilosopher(char id, Object table) {
    this.id = id;
    this.table = table;
    this.eating = false;
  }

  public void setLeft(SynchronizedPhilosopher left) {
    this.left = left;
  }

  public void setRight(SynchronizedPhilosopher right) {
    this.right = right;
  }

  @Override
  public void run() {
    try {
      while (!Thread.currentThread().isInterrupted()) {
        think();
        eat();
      }
    } catch (InterruptedException ex) {
      synchronized (table) {
        eating = false;
        table.notifyAll();
      }
      Thread.currentThread().interrupt();
    } finally {
      System.out.printf("Philosopher %s is leaving the table.%n", id);
    }
  }

  private void think() throws InterruptedException {
    synchronized (table) {
      eating = false;
      table.notifyAll();
    }
    System.out.println("Philosopher " + id + " thinks for a while");
    Thread.sleep(1000); // Simulate thinking
  }

  private void eat() throws InterruptedException {
    synchronized (table) {
      while (left.eating || right.eating) {
        table.wait();
      }
      eating = true;
    }
    System.out.println("Philosopher " + id + " eats for a while");
    Thread.sleep(1000); // Simulate eating
  }
}
