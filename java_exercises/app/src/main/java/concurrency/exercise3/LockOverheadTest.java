package concurrency.exercise3;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;

public class LockOverheadTest extends ApplicationFrame {

    public LockOverheadTest(String title) {
        super(title);
        DefaultCategoryDataset dataset = runBenchmark();
        JFreeChart chart = ChartFactory.createLineChart(
                "Lock Overhead Comparison",
                "Iterations",
                "Time (ms)",
                dataset
        );
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(panel);
    }

    // Unsynchronized method
    public int plainIncrement(int x) {
        return x + 1;
    }

    // Synchronized method
    public synchronized int synchronizedIncrement(int x) {
        return x + 1;
    }

    private DefaultCategoryDataset runBenchmark() {
        int[] iterationCounts = {
                100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000, 100_000_000_0
        };

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int dummy = 0;

        for (int ITERATIONS : iterationCounts) {
            // Warm up
            for (int i = 0; i < 100_000_000; i++) {
                this.plainIncrement(i);
                this.synchronizedIncrement(i);
            }

            // Plain timing
            long startPlain = System.nanoTime();
            for (int i = 0; i < ITERATIONS; i++) {
                dummy += this.plainIncrement(i);
            }
            long endPlain = System.nanoTime();
            long plainTime = (endPlain - startPlain) / 1_000_000;

            // Synchronized timing
            long startSync = System.nanoTime();
            for (int i = 0; i < ITERATIONS; i++) {
                dummy += this.synchronizedIncrement(i);
            }
            long endSync = System.nanoTime();
            long syncTime = (endSync - startSync) / 1_000_000;

            System.out.println(ITERATIONS + " sync: " + syncTime + " ms" + " plain: " + plainTime + " ms");

            dataset.addValue(plainTime, "Plain", String.valueOf(ITERATIONS));
            dataset.addValue(syncTime, "Synchronized", String.valueOf(ITERATIONS));
        }

        // Prevent dead code elimination
        System.out.println("Dummy: " + dummy);

        return dataset;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LockOverheadTest chart = new LockOverheadTest("Lock Overhead Performance Test");
            chart.pack();
            chart.setLocationRelativeTo(null);
            chart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            chart.setVisible(true);
        });
    }
}
