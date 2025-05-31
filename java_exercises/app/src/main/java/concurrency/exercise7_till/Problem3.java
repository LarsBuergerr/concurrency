package concurrency.exercise7_till;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

public class Problem3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MyCompletionService completionService = new MyCompletionService();

        // Submit tasks with different completion times
        completionService.submit(() -> {
            Thread.sleep(3000);
            return "Task 1 (slow)";
        });

        completionService.submit(() -> {
            Thread.sleep(1000);
            return "Task 2 (fast)";
        });

        completionService.submit(() -> {
            Thread.sleep(2000);
            return "Task 3 (medium)";
        });

        System.out.println("Submitted 3 tasks");

        // Process results as they complete (not in submission order)
        for (int i = 0; i < 3; i++) {
            MyFuture completed = completionService.take();
            System.out.println("Completed: " + completed.get());
            System.out.println("Completed tasks so far: " + completionService.completedTaskCount());
        }
        completionService.shutdown();
    }

}

class MyCompletionService {
    private final BlockingQueue<MyFuture> queue;
    private final MyExecutorProblemTwo executor;
    private int  completedTaskCount = 0;

    public MyCompletionService() {
        this.queue = new LinkedBlockingQueue<MyFuture>();
        this.executor = new MyExecutorProblemTwo(4, List.of());
    }

    public MyFuture submit(Callable task) {
        MyFuture future = new ExtendedFuture(task);
        executor.execute(future);
        return future;
    }

    public MyFuture take() throws InterruptedException {
        return queue.take();
    }

    public int completedTaskCount() {
        return completedTaskCount;
    }

    public void shutdown() {
        executor.shutDown();
    }

    private class ExtendedFuture extends MyFuture {

        public ExtendedFuture(Callable<Object> callable) {
            super(callable);
        }

        @Override
        public void run() {
            super.run();
            queue.offer(this);
            completedTaskCount++;
        }
    }

}


