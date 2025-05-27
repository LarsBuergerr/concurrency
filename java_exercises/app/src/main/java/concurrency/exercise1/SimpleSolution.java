package concurrency.exercise1;
import java.util.Date;


public class SimpleSolution {
    public static void main(String[] args) throws Exception {
        System.out.println("Testetstsets");


        int workload = 10;     // 10 seconds workload
        
        double fraction = 0.5; // fraction of the serializable part of the workload
        int numProcessors = 100; // number of processors

        double serializableWorkload = workload * fraction;
        double parallelizableWorkload = (workload - serializableWorkload) / numProcessors;

        Date start = new Date();

        System.out.println("Working on serializable part for " + serializableWorkload + " seconds");
        Thread.sleep((long) (serializableWorkload * 1000));

        System.out.println("Working on parallelizable part for " + parallelizableWorkload + " seconds");

        Thread.sleep((long) (parallelizableWorkload * 1000));

        Date end = new Date();

        System.out.println("Done");
        System.out.println("Total time: " + (end.getTime() - start.getTime()) / 1000 + " seconds");
    }
}