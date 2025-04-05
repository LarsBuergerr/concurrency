package concurrency.exercise3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Problem4 {
    public static void main(String[] args) {
        Cache cache = new Cache();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        Task t1 = new Task(cache);
        Task t2 = new Task(cache);
        Task t3 = new Task(cache);

        Request r1 = new Request(49);
        Request r2 = new Request(36);
        Request r3 = new Request(25);

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
                System.out.println("Response 1: " + cache.lastNum + " <-- --> " + Arrays.toString(cache.lastFactors) + " " + Arrays.toString(res1.factors));
            });
            executor.execute(() -> {
                try {
                    t2.service(r2, res2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Response 2: " + cache.lastNum + " <-- --> " + Arrays.toString(cache.lastFactors) + " " + Arrays.toString(res2.factors));
            });
            executor.execute(() -> {
                try {
                    t3.service(r3, res3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Response 3: " + cache.lastNum + " <-- --> " + Arrays.toString(cache.lastFactors) + " " + Arrays.toString(res3.factors));
            });

        }
        executor.shutdown();

    }
}

class Task {

    private Cache cache;

    public Task(Cache cache) {
        this.cache = cache;
    }

    Response service(Request req, Response res) throws InterruptedException {
        int i = extractFromRequest(req);
        Thread.sleep(10);
        synchronized (this) {
            if (i == cache.lastNum) {
                return encodeIntoResponse(res, cache.lastFactors);
            } else {
                int[] factors = factorsOf(i);
                Thread.sleep(10);
                cache.lastNum = i;
                cache.lastFactors = factors;
                return encodeIntoResponse(res, factors);
            }
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

class Response {
    final int num;
    int[] factors;

    public Response(int num, int[] factors) {
        this.num = num;
        this.factors = factors;
    }
}

class Request {
    final int num;

    public Request(int num) {
        this.num = num;
    }
}

class Cache {
    int lastNum;
    int[] lastFactors;

    public Cache() {
        lastNum = 0;
        lastFactors = new int[0];
    }

    public void setCache(int num, int[] factors) {
        lastNum = num;
        lastFactors = factors;
    }

    @Override
    public String toString() {
        return "Current Cache: \n" + "Last Number: " + lastNum + "\nLast Factors: " + Arrays.toString(lastFactors);
    }
}
