package concurrency.exercise3;

public class LockOverheadTest {

    private static final int ITERATIONS = 100_000_000;

    // Unsynchronized method
    public int plainIncrement(int x) {
        return x + 1;
    }

    // Synchronized method
    public synchronized int synchronizedIncrement(int x) {
        return x + 1;
    }

    public static void main(String[] args) {
        LockOverheadTest test = new LockOverheadTest();

        // Warm up
        for (int i = 0; i < 1_000_000; i++) {
            test.plainIncrement(i);
            test.synchronizedIncrement(i);
        }

        // Measure plainIncrement
        long startPlain = System.nanoTime();
        int dummy = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            dummy += test.plainIncrement(i);
        }
        long endPlain = System.nanoTime();
        System.out.println("Plain method time: " + (endPlain - startPlain) / 1_000_000 + " ms");

        // Measure synchronizedIncrement
        long startSync = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            dummy += test.synchronizedIncrement(i);
        }
        long endSync = System.nanoTime();
        System.out.println("Synchronized method time: " + (endSync - startSync) / 1_000_000 + " ms");

        // Prevent dead code elimination
        System.out.println("Dummy value: " + dummy);
    }
}
