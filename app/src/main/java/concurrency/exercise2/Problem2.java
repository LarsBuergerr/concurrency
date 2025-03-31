package concurrency.exercise2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Problem2 {
    public static void main(String[] args) throws InterruptedException {
        Cache cache = new Cache();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        
        for (int i = 0; i < 1000000000; i++) {
            int[] randomNums = new int[]{6, 8};
            int randomIndex = (int) (Math.random() * randomNums.length);
            Request request = new Request(randomNums[randomIndex]);
            executor.execute(new Service(request, cache));
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        
        System.out.println(cache.toString());
    }
}

class Service implements Runnable {
    private final Request request;
    private final Cache cache;

    public Service(Request request, Cache cache) {
        this.request = request;
        this.cache = cache;
    }

    @Override
    public void run() {
        int lastNum = cache.lastNum;
        // a small sleep here causes the cache to be invalidated very often
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // thread 3 overwrites cache

        Integer[] lastFactors = cache.lastFactors;


        // remove this code to not use the cache
        if (request.content.equals(lastNum)) {
            System.out.println("Cache hit!");
            Response response = new Response(lastNum, lastFactors);
            System.out.println("The factors of " + response.num + " are: " + Arrays.toString(response.factors));
            return;
        }

        Integer[] factors = this.factorsOf(request.content);
        
        if ((lastNum != 0) && !validateFactors(lastNum, lastFactors)) {
            System.out.println("Incorrect factorization detected! Number: " + lastNum + " Computed Factors: " + Arrays.toString(lastFactors));
            System.exit(1);
        }
        
        cache.setCache(request.content, factors);
        Response response = new Response(lastNum, lastFactors);
        System.out.println("The factors of " + response.num + " are: " + Arrays.toString(response.factors));    
    }

    public Integer[] factorsOf(int val) {
        List<Integer> numArray = new ArrayList<>();
        int original = val;
        
        for (int i = 2; i <= Math.sqrt(val); i++) {
            while (val % i == 0) {
                numArray.add(i);
                val /= i;
            }
        }
        if (val > 1) {
            numArray.add(val);
        }
        return numArray.toArray(new Integer[0]);
    }

    public boolean validateFactors(int number, Integer[] factors) {
        int product = 1;
        for (int factor : factors) {
            product *= factor;
        }
        return product == number;
    }
}

class Request {
    final Integer content;

    public Request(Integer content) {
        this.content = content;
    }
}

class Response {
  final Integer num;
    final Integer[] factors;

    public Response(Integer num, Integer[] factors) {
        this.num = num;
        this.factors = factors;
    }
}

class Cache {
    volatile Integer lastNum;
    volatile Integer[] lastFactors;

    public Cache() {
        lastNum = 0;
        lastFactors = new Integer[0];
    }

    public void setCache(Integer num, Integer[] factors) {
        lastNum = num;
        lastFactors = factors;
    }

    @Override
    public String toString() {
        return "Current Cache: \n" + "Last Number: " + lastNum + "\nLast Factors: " + Arrays.toString(lastFactors);
    }
}
