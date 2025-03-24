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

class Processor extends Thread {
    private int id;
    private double workload;

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
    public static double runSimulation(int numProcessors, double workload, double fraction) {
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
        double totalTime = (end.getTime() - start.getTime()) / 1000.0;
        System.out.println("Total time: " + totalTime + " seconds");
        return totalTime;
    }

    public static void main(String[] args) {
        int[] numCores = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024};
        double workload = 1;
        double fraction = 0.5;
        double[] executionTimes = new double[numCores.length];

        for (int i = 0; i < numCores.length; i++) {
            executionTimes[i] = runSimulation(numCores[i], workload, fraction);
        }

        double[] speedup = new double[numCores.length];
        double baseTime = executionTimes[0];
        for (int i = 0; i < numCores.length; i++) {
            speedup[i] = ((baseTime / executionTimes[i]) - 1) * 100; // Convert to percentile speedup
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < numCores.length; i++) {
            dataset.addValue(speedup[i], "Speedup (%)", Integer.toString(numCores[i]));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Parallel Speedup vs. Number of Cores",
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
