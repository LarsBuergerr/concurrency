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
                System.out.println("Response 1: " + r1.num + " " + Arrays.toString(res1.factors));
                if (!Arrays.equals(new int[]{7, 7}, res1.factors)) {
                    System.out.println("Race condition! ");
                }
            });
            executor.execute(() -> {
                try {
                    t2.service(r2, res1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Response 2: " + r2.num + " " + Arrays.toString(res1.factors));
                if (!Arrays.equals(new int[]{11, 11}, res1.factors)) {
                    System.out.println("Race condition! ");
                }
            });
            executor.execute(() -> {
                try {
                    t3.service(r3, res1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Response 3: " + r3.num + " " + Arrays.toString(res1.factors));
                if (!Arrays.equals(new int[]{2,2,3,3}, res1.factors)) {
                    System.out.println("Race condition! ");
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

    Response service(Request req, Response res) throws InterruptedException {
        int i = extractFromRequest(req);
        Thread.sleep(10);
        SharedMemorySnapShot snapShot = sharedMemory.getSnapShot();
            if (i == snapShot.lastNum) {
                return encodeIntoResponse(res, snapShot.lastFactors);
            } else {
                int[] factors = factorsOf(i);
                Thread.sleep(10);
                snapShot.lastNum = i;
                snapShot.lastFactors = factors;
                return encodeIntoResponse(res, factors);
        }
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
        Thread.sleep(10);
        int lastNum = this.lastNum;
        return new SharedMemorySnapShot(lastNum, lastFactors);
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
