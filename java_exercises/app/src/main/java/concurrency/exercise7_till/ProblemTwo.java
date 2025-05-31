package concurrency.exercise7_till;

import java.util.List;
import java.util.concurrent.*;


public class ProblemTwo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        MyExecutorProblemTwo executor = new MyExecutorProblemTwo(4,
                List.of(
                        new TaskProblem1(1),
                        new TaskProblem1(2),
                        new TaskProblem1(3),
                        new TaskProblem1(4),
                        new TaskProblem1(5)
                )
        );
        Thread.sleep(1000);
        Future<Object> future = executor.submit(new TaskProblem1(9999));
        Thread.sleep(1000);
        System.out.println(future.get());
        executor.shutDown();
    }
}

class MyExecutorProblemTwo extends MyExecutor {

    public MyExecutorProblemTwo(int num_threads, List<TaskProblem1> tasks) {
        super(num_threads, tasks);
    }

    public Future<Object> submit(TaskProblem1 task) {
        MyFuture future = new MyFuture(task);
        execute(future);
        return future;
    }
}

class MyFuture implements Future<Object>, Runnable {
    private final Callable<Object> callable;
    private Object result;

    public MyFuture(Callable<Object> callable) {
        this.callable = callable;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return result;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    @Override
    public void run() {
        try {
            result = callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}




