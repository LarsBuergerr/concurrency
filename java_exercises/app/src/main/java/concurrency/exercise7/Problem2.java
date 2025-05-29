package concurrency.exercise7;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class Problem2 {
    public static void main(String[] args) throws Exception {
        Callable<Double> test = ()  -> {
            System.out.println(Thread.currentThread().getName() + " basic task");
            double i = 1.0;
            for (int k = 0; k < 10_00; k++) {
                i = i + k;
            }
            Thread.sleep(1_000);
            System.out.println(i + " final number");
            return i;
        };


        Callable<Double> calculatingTask = () -> {
            System.out.println(Thread.currentThread().getName() + " calculating task");
            double j = 23;
            for (int i = 0; i < 100_000_000; i++) {
                j = j * 2 / 2.4;
            }
            System.out.println(j);
            return j;
        };

        MyExecutorProblem2 executor = new MyExecutorProblem2(List.of(test, calculatingTask));

        executor.runQueue();
        executor.add(test);
        executor.add(calculatingTask);

        executor.runQueue();

        executor.addMultiple(List.of(test, calculatingTask, test, calculatingTask));
        executor.runQueue();
        executor.add(() -> {
            return 2.9;
        });
        executor.runQueue();

        Future<Double> futureResult = executor.runFutureTask(calculatingTask);
        System.out.println("Future Result: " + futureResult.get());
        executor.finishTasks();

    }
}

class MyExecutorProblem2 {

    private static final int THREAD_NUMBER = 4;
    private final ExecutorService exec;
    Queue<Callable<Double>> taskQueue;

    public MyExecutorProblem2(List<Callable<Double>> tasks) {
        this.exec = Executors.newFixedThreadPool(THREAD_NUMBER);
        this.taskQueue = new ArrayDeque<>(tasks);
    }

    public void runQueue() throws ExecutionException, InterruptedException {
        while (!taskQueue.isEmpty()) {
            Callable<Double> task = taskQueue.poll();
            Future<Double> r = exec.submit(task);
            System.out.println(Thread.currentThread().getName() + " executing task " +  r.isDone() + " " + r.get());
        }
    }

    public void addMultiple(List<Callable<Double>> tasks) {
        taskQueue.addAll(tasks);
    }

    public void add(Callable<Double> r) {
        this.taskQueue.add(r);
    }

    public Future<Double> runFutureTask(Callable<Double> task) {
        return exec.submit(task);
    }

    public void finishTasks() {
        exec.shutdown();
    }
}
