package concurrency.exercise2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Problem2 {
  

  public static void main(String[] args) {
    Cache cache = new Cache();
    Request request = new Request(20);
    Service service = new Service(request, cache);
    service.run();

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
    Integer[] factors = this.factorsOf(request.content);

    cache.setCache(request.content, factors);
    Response response = new Response(factors);
    System.out.println("The factors of " + request.content + " are: " + Arrays.toString(response.content));    
  }

  public Integer[] factorsOf(int val) {
    List<Integer> numArray = new ArrayList<Integer>();

    for (int i = 2; i <= Math.ceil(Math.sqrt(val)); i++) {
        if (val % i == 0) {
            numArray.add(i);
            val /= i;
        }
    }
    numArray.add(val);
    return numArray.toArray(new Integer[numArray.size()]);
  }
}


class Request {
  final Integer content;

  public Request(Integer content) {
    this.content = content;
  }
}

class Response {
  final Integer[] content;

  public Response(Integer[] content) {
    this.content = content;
  }
}

class Cache {
  Integer lastNum;
  Integer[] lastFactors;

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