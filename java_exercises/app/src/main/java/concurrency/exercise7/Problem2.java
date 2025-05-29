package concurrency.exercise7;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

public class Problem2 {

  public static void main(String[] args) throws InterruptedException {
    Executor66 executor = new Executor66(3);

    for (int i = 0; i < 10; i++) {

      int randomTimetoProcess = (int) (Math.random() * 10 + 1);
      Task task = new Task(i, randomTimetoProcess);
      executor.execute(task);
    }

    Thread.sleep(20000);
    System.out.println("Shutting down executor...");
    executor.shutdown();
  }
}

class Executor66 {

  private final BlockingQueue<Task> queue;
  private final Thread[] workers;
  private volatile boolean isShutdown = false;

  public Executor66(int poolSize) {
    this.queue = new LinkedBlockingQueue<Task>();
    this.workers = new Thread[poolSize];

    for (Thread worker : workers) {
      worker = new Thread(() -> {
        while (true) {
          try {
            Task task = queue.take();
            if (task instanceof PoisonPill) {
              System.out.printf("Thread %d received poison pill, shutting down%n", Thread.currentThread().threadId());
              break;
            }
            task.run();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          }
        }
      });
      worker.start();
    }
  }

  public void execute(Task task) {
    if (isShutdown) {
      throw new IllegalStateException("Executor is shutdown, cannot accept new tasks");
    }

    try {
      System.out.printf("Adding task %d to the queue%n", task.id);
      queue.put(task);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Failed to add task to the queue", e);
    }
  }

  public void shutdown() {
    isShutdown = true;
    for (int i = 0; i < workers.length; i++) {
      queue.offer(new PoisonPill()); // send a termination signal
    }
  }
}

class PoisonPill extends Task {
  public PoisonPill() {
    super(-1, 0);
  }

  @Override
  public void run() {
  }
}

class Task implements Runnable, Callable {
  final int id;
  final int timeToProcess;

  public Task(int id, int timeToProcess) {
    this.id = id;
    this.timeToProcess = timeToProcess;
  }

  @Override
  public void run() {
    try {
      System.out.printf("Thread %d is processing Task %d for %d seconds%n", Thread.currentThread().threadId(), id,
          timeToProcess);
      Thread.sleep(timeToProcess * 1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.out.printf("Thread %d was interrupted while processing Task %d%n", Thread.currentThread().threadId(), id);
    }
  }

  @Override
  public Object call() {
    return new Object();
  }
}