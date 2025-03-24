package concurrency.exercise1;

import java.util.Date;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Chart {
    public static void main(String[] args) throws InterruptedException {
        int[] numCores = {1, 2, 3, 4, 5, 6};
        double[] executionTimes = new double[numCores.length];
        double totalWorkload = 10;
        double fractionSerial = 0.5;
        
        // Run simulation for each core count
        for (int i = 0; i < numCores.length; i++) {
            Date start = new Date();
            ComplexSolution simulation = new ComplexSolution(fractionSerial, numCores[i], (int) totalWorkload, false);
            simulation.runSimulation();
            Date end = new Date();
            executionTimes[i] = (end.getTime() - start.getTime()) / 1000.0;
        }
        
        // Compute speedup percentages
        double[] speedup = new double[numCores.length];
        double baseTime = executionTimes[0];
        for (int i = 0; i < numCores.length; i++) {
            speedup[i] = (baseTime / executionTimes[i]) * 100;
        }
        
        // Create chart dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < numCores.length; i++) {
            dataset.addValue(speedup[i], "Speedup (%)", Integer.toString(numCores[i]));
        }
        
        // Generate chart
        JFreeChart chart = ChartFactory.createLineChart(
                "Parallel Speedup vs. Number of Cores",
                "Number of Cores",
                "Speedup (%)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
        
        // Display chart in a window
        JFrame frame = new JFrame("Speedup Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }
}
