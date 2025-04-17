package concurrency.exercise3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Problem5 {
    public static void main(String[] args) {
        SharedMemory sharedMemory = new SharedMemory();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        Action t1 = new Action(sharedMemory);
        Action t2 = new Action(sharedMemory);
        Action t3 = new Action(sharedMemory);

        Request r1 = new Request(49);
        Request r2 = new Request(121);
        Request r3 = new Request(36);

        Response res1 = new Response(0, new int[0]);
        Response res2 = new Response(0, new int[0]);
        Response res3 = new Response(0, new int[0]);


        for (int i = 0; i < 1000; i++) {
            executor.execute(() -> {
                try {
                    t1.service(r1, res1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            executor.execute(() -> {
                try {
                    t2.service(r2, res2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            executor.execute(() -> {
                try {
                    t3.service(r3, res3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

        }
        executor.shutdown();

    }
}

class Action {

    private final SharedMemory sharedMemory;

    public Action(SharedMemory sharedMemory) {
        this.sharedMemory = sharedMemory;
    }

    void service(Request req, Response res) throws InterruptedException {
        Response response;
        int i = extractFromRequest(req);
        SharedMemorySnapShot snapShot = sharedMemory.getSnapShot();

        if (i == snapShot.lastNum) {
            response = encodeIntoResponse(res, snapShot.lastFactors);
        } else {
            int[] factors = factorsOf(i);
            sharedMemory.setCache(i, factors);
            response = encodeIntoResponse(res, factors);
        }
        System.out.println("i: " + i);
        System.out.println("Factors " + Arrays.toString(response.factors));

        if(!this.validateFactors(i, response.factors)) {
            System.out.println("Incorrect factorization detected! Number: " + i + " Computed Factors: " + Arrays.toString(response.factors));
            System.exit(1);
        } else {
            System.out.println("The factors of " + i + " are: " + Arrays.toString(response.factors));
        }
    }

    public boolean validateFactors(int number, int[] factors) {
        int product = 1;
        for (int factor : factors) {
            product *= factor;
        }
        return product == number;
    }


    private Response encodeIntoResponse(Response res, int[] factors) {
        res.factors = factors;
        return res;
    }

    private int extractFromRequest(Request req) {
        return req.num;
    }

    private int[] factorsOf(int val) {
        List<Integer> numArray = new ArrayList<>();

        for (int i = 2; i <= Math.sqrt(val); i++) {
            while (val % i == 0) {
                numArray.add(i);
                val /= i;
            }
        }
        if (val > 1) {
            numArray.add(val);
        }
        return numArray.stream().mapToInt(i->i).toArray();
    }

}

class SharedMemory {
    int lastNum;
    int[] lastFactors;

    public SharedMemory() {
        lastNum = 0;
        lastFactors = new int[0];
    }

    public synchronized SharedMemorySnapShot getSnapShot() throws InterruptedException {
        int[] lastFactors = this.lastFactors;
        int lastNum = this.lastNum;
        return new SharedMemorySnapShot(lastNum, lastFactors);
    }

    public synchronized void setCache(int lastNum, int[] lastFactors) {
        this.lastNum = lastNum;
        this.lastFactors = lastFactors;
    }

    @Override
    public String toString() {
        return "Current Cache: \n" + "Last Number: " + lastNum + "\nLast Factors: " + Arrays.toString(lastFactors);
    }
}

class SharedMemorySnapShot {
    int lastNum;
    int[] lastFactors;

    public SharedMemorySnapShot(int lastNum, int[] lastFactors) {
        this.lastFactors = lastFactors;
        this.lastNum = lastNum;
    }
}
