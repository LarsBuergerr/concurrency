package concurrency.exercise5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Problem3 {
  public static void main(String[] args) {
    Cache cache = new Cache(0, new int[0]);
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    for (int i = 0; i < 1000; i++) {
      int[] randomNums = new int[]{6, 8};
      int randomIndex = (int) (Math.random() * randomNums.length);
      Request request = new Request(randomNums[randomIndex]);
      executor.execute(new Service(request, cache));
    }
    executor.shutdown();
  }
}


class Service implements Runnable {
  Request request;
  Cache cache;

  public Service(Request request, Cache cache) {
    this.request = request;
    this.cache = cache;
  }

  @Override
  public void run() {
    int i = request.num;
    Cache currentCache = cache.getCache();

    if (i == currentCache.lastNum) {
      Response response = new Response(i, currentCache.lastFactors);
      System.out.println("Cache Hit!  - The factors of " + response.num + " are: " + Arrays.toString(response.factors));
    } else {
      int[] factors = factorsOf(i);
      cache.setCache(i, factors);
      Response response = new Response(i, factors);
      System.out.println("Cache Miss! - The factors of " + response.num + " are: " + Arrays.toString(response.factors));
    }

    if(!validateFactors(currentCache.lastNum, currentCache.lastFactors)) {
      System.out.println("Incorrect factorization detected! Number: " + currentCache.lastNum + " Computed Factors: " + Arrays.toString(currentCache.lastFactors));
      System.exit(1);
    }
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

  public boolean validateFactors(int number, int[] factors) {
    if (number == 0) {
      return true;
    }
    int product = 1;
    for (int factor : factors) {
      product *= factor;
    }
    return product == number;
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
  private ReadWriteLock lock = new ReentrantReadWriteLock();
  Lock readLock = lock.readLock();
  Lock writeLock = lock.writeLock();

  public Cache(int lastNum, int[] lastFactors) {
    this.lastNum = lastNum;
    this.lastFactors = lastFactors;
  }

  public void setCache(int num, int[] factors) {
    writeLock.lock();
    try {
      lastNum = num;
      lastFactors = factors;
    } finally {
      writeLock.unlock();
    }
  }

  public Cache getCache() {
    readLock.lock();
    try {
      return new Cache(lastNum, lastFactors);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public String toString() {
    return "Current Cache: \n" + "Last Number: " + lastNum + "\nLast Factors: " + Arrays.toString(lastFactors);
  }
}
