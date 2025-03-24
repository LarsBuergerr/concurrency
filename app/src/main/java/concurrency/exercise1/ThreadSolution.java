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
        int maxCores = 256;
        double workload = 1;
        double[] fractions = {0.5, 0.25, 0.1, 0.05};
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (double fraction : fractions) {
            double[] executionTimes = new double[maxCores];
            for (int i = 1; i < maxCores; i++) {
                if (i > 64 && i % 2 == 1) {
                    continue;
                }
                executionTimes[i] = runSimulation(i, workload, fraction);
            }

            double baseTime = workload;
            for (int i = 1; i < maxCores; i++) {
                if (i > 64 && i % 2 == 1) {
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
