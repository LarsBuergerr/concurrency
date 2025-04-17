package concurrency.exercise3;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Problem2 {
    public static void main(String[] args) throws MalformedURLException, IOException, InterruptedException {
        URL url1 = new URL("http://212.183.159.230/100MB.zip");
        URL url2 = new URL("http://212.183.159.230/100MB.zip");

        Downloader downloader1 = new Downloader(url1, "download1.bin", "D1");
        Downloader downloader2 = new Downloader(url2, "download2.bin", "D2");

        DeadlockListener listener1 = new DeadlockListener(downloader2, "L1");
        DeadlockListener listener2 = new DeadlockListener(downloader1, "L2");

        downloader1.addListener(listener1);
        downloader2.addListener(listener2);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        System.out.println("Starting downloader 1...");
        executor.execute(() -> {
            try {
                downloader1.run();
                System.out.println("Downloader 1 finished (unexpectedly if deadlock occurs)");
            } catch (IOException e) {
                System.err.println("Downloader 1 Error: " + e.getMessage());
            }
        });

        System.out.println("Starting downloader 2...");
        executor.execute(() -> {
            try {
                downloader2.run();
                System.out.println("Downloader 2 finished (unexpectedly if deadlock occurs)");
            } catch (IOException e) {
                System.err.println("Downloader 2 Error: " + e.getMessage());
            }
        });

        executor.shutdown();
        boolean terminated = executor.awaitTermination(30, TimeUnit.SECONDS);
        if (!terminated) {
            System.out.println("\n---> DEADLOCK DETECTED (Executor did not terminate) <---");
            executor.shutdownNow(); // Force shutdown
        } else {
            System.out.println("\n---> Execution Finished (No Deadlock Detected) <---");
        }
        System.out.println("Main Done");
    }
}

class Downloader {
    private final InputStream in;
    private final OutputStream out;
    private final List<ProgressListener> listeners;
    private final String name;

    public Downloader(URL url, String outputFilename, String name) throws IOException {
        System.out.println(name + ": Connecting to " + url);
        try {
            in = url.openConnection().getInputStream();
            out = new FileOutputStream(outputFilename);
            listeners = new ArrayList<>();
            this.name = name;
            System.out.println(name + ": Connected.");
        } catch (IOException e) {
            System.err.println(name + ": Failed to connect/open file - " + e);
            throw e;
        }
    }

    public synchronized void addListener(ProgressListener listener) {
        System.out.println(name + ": Adding listener " + listener);
        listeners.add(listener);
    }

    public synchronized void performAction(String callerName) {
        System.out.println("--> " + name + ": performAction() called by " + callerName + " on thread " + Thread.currentThread().getName());
        try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        System.out.println("<-- " + name + ": performAction() finished by " + callerName);
    }

    private synchronized void updateProgress(int n, int total) {
            System.out.println(name + ": updateProgress (+" + n + " = " + total + ") - Acquiring lock. Notifying listeners... on thread " + Thread.currentThread().getName());
            for (ProgressListener listener : listeners) {
                listener.onProgress(total);
            System.out.println(name + ": updateProgress - Releasing lock.");
        }
    }


    public void run() throws IOException {
        System.out.println(name + ": Starting download run() on thread " + Thread.currentThread().getName());
        int n = 0, total = 0;
        byte buffer[] = new byte[1024];
        try {
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
                total += n;
                updateProgress(n, total);
            }
            out.flush();
            System.out.println(name + ": Download finished.");
        } catch (IOException e){
            System.err.println(name + ": IO Error during download: " + e);
            throw e;
        } finally {
            try { in.close(); } catch (IOException e) { /* ignore */ }
            try { out.close(); } catch (IOException e) { /* ignore */ }
            System.out.println(name + ": Streams closed.");
        }
    }

    @Override
    public String toString() {
        return "Downloader[" + name + "]";
    }
}

interface ProgressListener {
    void onProgress(int progress);
}

class DeadlockListener implements ProgressListener {
    private final Downloader otherDownloader;
    private final String name;

    public DeadlockListener(Downloader other, String name) {
        this.otherDownloader = other;
        this.name = name;
    }

    @Override
    public void onProgress(int progress) {
        System.out.println(name + ": onProgress(" + progress + ") - Attempting to call performAction() on " + otherDownloader + " from thread " + Thread.currentThread().getName());
        otherDownloader.performAction(this.name);
        System.out.println(name + ": onProgress(" + progress + ") - Returned from performAction() on " + otherDownloader);
    }

    @Override
    public String toString() {
        return "DeadlockListener[" + name + " -> " + otherDownloader + "]";
    }
}