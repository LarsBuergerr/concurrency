package concurrency.exercise7;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Problem2 {

  public static void main(String[] args) throws Exception {
    Executor executor = new Executor(3);
    MyCompletionService<Integer> completionService = new MyCompletionService<>(executor);

    for (int i = 0; i < 10; i++) {
      final int taskId = i;
      int duration = (int) (Math.random() * 10 + 1);
      completionService.submit(() -> {
        System.out.printf("Callable Task %d is running for %d seconds%n", taskId, duration);
        Thread.sleep(duration * 1000);
        return taskId * 2;
      });
    }

    for (int i = 0; i < 10; i++) {
      Future<Integer> future = completionService.take();
      System.out.println("Result received: " + future.get());
    }

    executor.shutdown();
  }
}

// --------------------- EXECUTOR ---------------------
class Executor {
  private final BlockingQueue<Runnable> queue;
  private final Thread[] workers;
  private final AtomicBoolean isShutdown = new AtomicBoolean(false);

  public Executor(int poolSize) {
    this.queue = new LinkedBlockingQueue<>();
    this.workers = new Thread[poolSize];

    for (int i = 0; i < poolSize; i++) {
      workers[i] = new Thread(() -> {
        try {
          while (true) {
            Runnable task = queue.take();
            if (task instanceof PoisonPillRunnable) {
              System.out.printf("Thread %d received poison pill, shutting down%n", Thread.currentThread().threadId());
              break;
            }
            task.run();
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      });
      workers[i].start();
    }
  }

  public void execute(Runnable task) {
    if (isShutdown.get()) {
      throw new IllegalStateException("Executor is shutdown, cannot accept new tasks");
    }

    try {
      queue.put(task);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Failed to add task to queue", e);
    }
  }

  public <T> Future<T> submit(Callable<T> callable) {
    FutureTask<T> futureTask = new FutureTask<>(callable);
    execute(futureTask);
    return futureTask;
  }

  public void shutdown() {
    isShutdown.set(true);
    for (int i = 0; i < workers.length; i++) {
      queue.offer(new PoisonPillRunnable());
    }
  }
}

// Poison pill to stop workers
class PoisonPillRunnable implements Runnable {
  @Override
  public void run() {
  }
}

// --------------------- COMPLETION SERVICE ---------------------
class MyCompletionService<T> {
  private final Executor executor;
  private final BlockingQueue<Future<T>> completionQueue = new LinkedBlockingQueue<>();

  public MyCompletionService(Executor executor) {
    this.executor = executor;
  }

  public Future<T> submit(Callable<T> task) {
    CompletionTask<T> completionTask = new CompletionTask<>(task, completionQueue);
    executor.execute(completionTask);
    return completionTask;
  }

  public Future<T> take() throws InterruptedException {
    return completionQueue.take();
  }

  public Future<T> poll() {
    return completionQueue.poll();
  }
}

// Wraps a Callable and puts its Future in the completion queue when done
class CompletionTask<T> extends FutureTask<T> {
  private final BlockingQueue<Future<T>> completionQueue;

  public CompletionTask(Callable<T> callable, BlockingQueue<Future<T>> completionQueue) {
    super(callable);
    this.completionQueue = completionQueue;
  }

  @Override
  protected void done() {
    completionQueue.add(this);
  }
}
