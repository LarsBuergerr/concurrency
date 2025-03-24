package concurrency.exercise1;

import java.util.Date;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;


/**
 * 
 * Aufgabe 1
 * 
 * Assume 1% of the runtime of a program is not parallelizable. How much speed-up can
 * be achieved by execution on 64 cores, assuming there is no additional overhead for the
 * parallel execution?
 * 
 * S = 1 / (F + (1 - F) / P)
 * => 1 / (0.01 + 0.99 / 64) == 39.26
 * 
 * 
 * Aufgabe 2
 * 
 * This time, assume the program above uses a broadcast operation that incurs an overhead
 * that depends on the number of used cores, P . This overhead is 0.0001 · P . For which
 * number of cores do you get the highest speedup?
 * 
 * 
 * 1 / (0.01 + 0.99 / P) + 0.0001 * P
 * d/dP (1 / (0.01 + 0.99 / P) + 0.0001 * P) = 0
 * 
 * => -0.99 / P^2 + 0.0001 = 0
 * => 0.0001 = 0.99 / P^2
 * => P^2 = 0.99 / 0.0001
 * => P = sqrt(0.99 / 0.0001) == 99.498743
 */




class Processor extends Thread {
    private int id;
    private double workload;
    private boolean debug;

    public Processor(int id, double workload, boolean debug) {
        this.id = id;
        this.workload = workload;
        this.debug = debug;
    }

    public void run() {
        try {
            if (debug) {
            System.out.println("Processor " + id + " working for " + workload + " seconds");
            }
            Thread.sleep((long) (workload * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class ThreadSolution {
    public static double runSimulation(int numProcessors, double workload, double fraction) {
        double serializableWorkload = workload * fraction;
        double parallelizableWorkload = (workload - serializableWorkload) / numProcessors;
        Processor[] processors = new Processor[numProcessors];

        Date start = new Date();
        try {
            // System.out.println("Working on serializable part for " + serializableWorkload + " seconds");
            Thread.sleep((long) (serializableWorkload * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < numProcessors; i++) {
            processors[i] = new Processor(i, parallelizableWorkload, false);
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
        double totalTime = (end.getTime() - start.getTime()) / 1000.0;
        System.out.println("Total time: " + totalTime + " seconds");
        return totalTime;
    }

    public static void main(String[] args) {
        int maxCores = 512;
        double workload = 2;
        double[] fractions = {0.5, 0.25, 0.1, 0.05};
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (double fraction : fractions) {
            double[] executionTimes = new double[maxCores];
            for (int i = 1; i < maxCores; i++) {
                if ((i > 32 && i % 2 == 1) || (i > 64 && i % 4 != 0) || (i > 128 && i % 8 != 0)) {
                    continue;
                }
                executionTimes[i] = runSimulation(i, workload, fraction);
            }

            double baseTime = workload;
            for (int i = 1; i < maxCores; i++) {
                if ((i > 32 && i % 2 == 1) || (i > 64 && i % 4 != 0) || (i > 128 && i % 8 != 0)) {
                    continue;
                }
                double speedup = ((baseTime / executionTimes[i]));
                dataset.addValue(speedup, "Fraction " + fraction, Integer.toString(i));
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Amdahls Law",
                "Number of Cores",
                "Speedup (%)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);



        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setAutoRangeIncludesZero(true);

        JFrame frame = new JFrame("Speedup Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }
}
