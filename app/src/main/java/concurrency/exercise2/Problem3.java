package concurrency.exercise2;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Problem3 {
    public static void main(String[] args) {
        Chain chain = new Chain();
        ExecutorService executor = Executors.newFixedThreadPool(chain.NUM_THREADS);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < chain.NUM_THREADS; i++) {
            executor.execute(new Miner(i, chain));
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        chain.executionTime = endTime - startTime;
        System.out.println(chain);
    }
}

class Chain {
    public final String BLOCK = "new block";
    public final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    public final BigInteger TARGET = BigInteger.valueOf(2).pow(226);
    // 230 ~ 2300ms execution time
    // 228 ~ 8692ms execution time
    // 227 ~ 16484ms execution time
    // 226 ~ 53750ms execution time

    public volatile boolean found = false;
    public volatile int winningNonce = -1;
    public volatile long executionTime = 0;

    @Override
    public String toString() {
        return "=== Proof of Work ===\n" +
               "Winning Nonce: " + winningNonce + "\n" +
               "Execution Time: " + executionTime + " ms\n";
    }
}

class Miner implements Runnable {
    private final int threadId;
    private final Chain chain;

    public Miner(int threadId, Chain chain) {
        this.threadId = threadId;
        this.chain = chain;
    }

    @Override
    public void run() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            int nonce = threadId;
            while (!chain.found) {
                String input = chain.BLOCK + nonce;
                byte[] hash = md.digest(md.digest(input.getBytes())); // Double hashing
                BigInteger hashInt = new BigInteger(1, hash);

                if (hashInt.compareTo(chain.TARGET) < 0) {
                    chain.found = true;
                    chain.winningNonce = nonce;
                    break;
                }
                nonce += chain.NUM_THREADS; // Distribute work among threads
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
