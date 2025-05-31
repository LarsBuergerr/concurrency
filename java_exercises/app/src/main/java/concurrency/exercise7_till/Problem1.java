package concurrency.exercise7_till;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;


public class Problem1 {

    public static void main(String[] args) throws InterruptedException {
        MyExecutor executor = new MyExecutor(4,
                List.of(
                new TaskProblem1(1),
                new TaskProblem1(2),
                new TaskProblem1(3),
                new TaskProblem1(4),
                new TaskProblem1(5)
                )
        );
        Thread.sleep(5000);
        executor.shutDown();
    }
}

class MyExecutor {
    private final Queue<Runnable> queue;
    private final List<Thread> workers;
    private boolean done = false;


    public MyExecutor(int num_threads, List<TaskProblem1> tasks) {
        queue = new LinkedBlockingQueue<>();
        workers = new ArrayList<>();
        queue.addAll(tasks);
        for (int i = 0; i < num_threads; i++) {
            workers.add(new Thread(new Worker()));
            workers.get(i).start();
        }
    }

    public void execute(Runnable task) {
        queue.add(task);
        //queue.offer(task);
    }

    public void shutDown() {
        System.out.println("Shutting down " + queue.size());
        done = true;
        for (Thread worker : workers) {
            worker.interrupt();
        }
    }

    private class Worker implements Runnable {

        @Override
        public void run() {
            while (!done) {
                Runnable task = queue.poll();
                if (task != null) {
                    task.run();
                }
            }
        }
    }
}

class TaskProblem1 implements Runnable, Callable<Object> {

    private final int id;

    public TaskProblem1(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
        System.out.println(id);
    }

    @Override
    public Object call() throws Exception {
        return new Object();
    }
}


