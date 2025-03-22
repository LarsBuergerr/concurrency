
import java.util.Date;


class Processor extends Thread {
  private int id;
  private double workload;
  private boolean isFinished = false;


  public Processor(int id, double workload) {
      this.id = id;
      this.workload = workload;
  }

  public void run() {
      try {
          System.out.println("Processor " + id + " working for " + workload + " seconds");
          Thread.sleep((long) (workload * 1000));
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
  }
}


public class ThreadSolution {

  public static void main(String[] args) {
      
      int workload = 10;     // 10 seconds workload
      
      double fraction = 0.5; // fraction of the serializable part of the workload
      int numProcessors = 2; // number of processors

      double serializableWorkload = workload * fraction;
      double parallelizableWorkload = (workload - serializableWorkload) / numProcessors;

      Processor[] processors = new Processor[numProcessors];

      Date start = new Date();

      try {
          System.out.println("Working on serializable part for " + serializableWorkload + " seconds");
          Thread.sleep((long) (serializableWorkload * 1000));
      } catch (InterruptedException e) {
          e.printStackTrace();
      }

      for (int i = 0; i < numProcessors; i++) {
          processors[i] = new Processor(i, parallelizableWorkload);
          processors[i].start();
      }

      for (int i = 0; i < numProcessors; i++) {
          try {
              processors[i].join();
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }

      Date end = new Date(); 
      System.out.println("Done");

      System.out.println("Total time: " + (end.getTime() - start.getTime()) / 1000 + " seconds");
  }
}