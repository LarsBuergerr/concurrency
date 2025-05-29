package concurrency.exercise7;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Problem1 {

    public static void main(String[] args) {
        Runnable test = () -> {
            System.out.println(Thread.currentThread().getName() + " basic task");
            int i = 1;
            for (int k = 0; k < 10_000; k++) {
                i = i + k;
            }
            System.out.println(i + " final number");
        };

        Runnable calculatingTask = () -> {
            System.out.println(Thread.currentThread().getName() + " calculating task");
            double j = 23;
            for (int i = 0; i < 100_000_000; i++) {
                j = j * 2 / 2.4;
            }
            System.out.println(j);
        };
        MyExecutor executor = new MyExecutor(List.of(test, calculatingTask));

        executor.runQueue();
        executor.add(test);
        executor.add(calculatingTask);

        executor.runQueue();

        executor.addMultiple(List.of(test, calculatingTask, test, calculatingTask));
        executor.runQueue();
        executor.finishTasks();
    }
}

class MyExecutor {

    private static final int THREAD_NUMBER = 4;
    private final ExecutorService exec;
    Queue<Runnable> taskQueue;

    public MyExecutor(List<Runnable> tasks) {
        this.exec = Executors.newFixedThreadPool(THREAD_NUMBER);
        this.taskQueue = new ArrayDeque<>(tasks);
    }

    public void runQueue() {
        while (!taskQueue.isEmpty()) {
            Runnable task = taskQueue.poll();
            exec.execute(task);
        }
    }

    public void addMultiple(List<Runnable> tasks) {
        taskQueue.addAll(tasks);
    }

    public void add(Runnable r) {
        this.taskQueue.add(r);
    }

    public void finishTasks() {
        exec.shutdown();
    }
}
