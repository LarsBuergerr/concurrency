package concurrency.exercise3;

import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;


public class Problem3withPlot extends JFrame {

    private static final String SERIES_SYNC = "Synchronized";
    private static final String SERIES_UNSYNC = "Unsynchronized";

    public Problem3withPlot(String title, DefaultCategoryDataset dataset) {
        super(title);

        JFreeChart lineChart = ChartFactory.createLineChart(
            "Synchronized vs Unsynchronized Performance",
            "Number of Loop Executions",
            "Execution Time (ms)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        CategoryPlot plot = lineChart.getCategoryPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setNumberFormatOverride(NumberFormat.getNumberInstance());
        rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(chartPanel);
    }

    public static void main(String[] args) throws InterruptedException {
        int[] executionTimesArray = {
            10,
            100,
            1000,
            10000,
            100000,
        };

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        System.out.println("Running performance tests...");
        System.out.println("ExecutionTimes | Sync Time (ms) | Unsync Time (ms)");
        System.out.println("---------------|----------------|------------------");

        for (int executionTimes : executionTimesArray) {
            System.out.printf("%14d | ", executionTimes);

            Synchronized synchronizedThread = new Synchronized(executionTimes);
            long startTimeSync = System.nanoTime();
            synchronizedThread.start();
            synchronizedThread.join();
            long endTimeSync = System.nanoTime();
            double syncDurationMs = (endTimeSync - startTimeSync) / 1_000_000.0;
            System.out.printf("%14.3f | ", syncDurationMs);

            dataset.addValue(syncDurationMs, SERIES_SYNC, Integer.toString(executionTimes));

            Unsynchronized unsynchronizedThread = new Unsynchronized(executionTimes);
            long startTimeUnsync = System.nanoTime();
            unsynchronizedThread.start();
            unsynchronizedThread.join();
            long endTimeUnsync = System.nanoTime();
            double unsyncDurationMs = (endTimeUnsync - startTimeUnsync) / 1_000_000.0;
            System.out.printf("%16.3f%n", unsyncDurationMs);

            dataset.addValue(unsyncDurationMs, SERIES_UNSYNC, Integer.toString(executionTimes));
        }
        System.out.println("Performance tests complete.");

        SwingUtilities.invokeLater(() -> {
          Problem3withPlot chartFrame = new Problem3withPlot(
                "Thread Performance Comparison",
                 dataset);
            chartFrame.pack();
            chartFrame.setLocationRelativeTo(null);
            chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            chartFrame.setVisible(true);
        });

        System.out.println("Chart window launched.");
    }
}