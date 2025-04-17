package concurrency.exercise1;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class SerialPart {
    private int workload;
    private final Lock lock = new ReentrantLock();

    public SerialPart(int workload) {
        this.workload = workload;
    }

    public boolean work(boolean debug, int processorId) throws InterruptedException {
        lock.lock();
        try {
          Thread.sleep(1000);
            if (workload > 0) {
                workload--;
                if (debug) {
                    System.out.println("Serial work done by Processor " + processorId + ", remaining: " + workload);
                }
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean isDone() {
        return workload == 0;
    }
}

class ParallelPart {
    private int workload;

    public ParallelPart(int workload) {
        this.workload = workload;
    }

    public synchronized boolean work(boolean debug, int processorId) throws InterruptedException {
        if (workload > 0) {
            workload--;
            if(debug) {
                System.out.println("Parallel work done by Processor " + processorId + ", remaining: " + workload);
            }
            return true;
        }
        return false;
    }
}

public class ComplexSolution {
    private final double fractionSerial;
    private final int numProcessors;
    private final int totalWorkload;
    private final boolean debug;

    public ComplexSolution(double fractionSerial, int numProcessors, int totalWorkload, boolean debug) {
        this.fractionSerial = fractionSerial;
        this.numProcessors = numProcessors;
        this.totalWorkload = totalWorkload;
        this.debug = debug;
    }

    public void runSimulation() throws InterruptedException {
        SerialPart serialPart = new SerialPart((int) (fractionSerial * totalWorkload));
        ParallelPart parallelPart = new ParallelPart((int) ((1 - fractionSerial) * totalWorkload));
        ExecutorService executor = Executors.newFixedThreadPool(numProcessors);

        CountDownLatch latch = new CountDownLatch(numProcessors);
        for (int i = 0; i < numProcessors; i++) {
            final int processorId = i; // Capture i in a final variable
            executor.execute(() -> {
                try {
                    while (true) {
                        boolean didSerialWork = serialPart.work(this.debug, processorId);
                        if (serialPart.isDone()) break;
                    }
                    while (parallelPart.work(this.debug, processorId)) {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        System.out.println("Simulation completed.");
    }

    public static void main(String[] args) throws InterruptedException {
        double fractionSerial = 0.5; // 20% serial
        int numProcessors = 2;
        int totalWorkload = 20;

        Date start = new Date();
        ComplexSolution simulation = new ComplexSolution(fractionSerial, numProcessors, totalWorkload, true);
        simulation.runSimulation();

        Date end = new Date();
        System.out.println("Total time: " + (end.getTime() - start.getTime()) / 1000 + " seconds");
    }
}
