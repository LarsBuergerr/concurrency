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

public class Problem2 {
    public static void main(String[] args) throws MalformedURLException, IOException {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < 10; i++) {
            executor.execute(new ThreadRunner());
        }
        executor.shutdown();
        System.out.println("Done");
    }
}

class ThreadRunner implements Runnable {

    @Override
    public void run() {
        try {
            URL url = new URL("http://212.183.159.230/100MB.zip");  // London public test server
            String outputFilename = "test_download.bin";
            Downloader downloader = new Downloader(url, outputFilename);
            downloader.addListener(new CustomListener());
            downloader.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


class Downloader {
    private final InputStream in;
    private final OutputStream out;
    private final List<ProgressListener> listeners;

    public Downloader(URL url, String outputFilename) throws IOException {
        in = url.openConnection().getInputStream();
        out = new FileOutputStream(outputFilename);
        listeners = new ArrayList<>();
    }

    public synchronized void addListener(ProgressListener listener) {
        listeners.add(listener);
    }

    private synchronized void updateProgress(int total) {
        //System.out.println("I want to update!");
        for (ProgressListener listener : listeners)
            listener.onProgress(total);
    }

    public void run() throws IOException {
        int n = 0, total = n;
        byte buffer[] = new byte[1024];
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
            total += n;
            updateProgress(total);
        }
        out.flush();
    }
}

interface ProgressListener {
    void onProgress(int progress);
}

class CustomListener implements ProgressListener {

    @Override
    public void onProgress(int progress) {
        System.out.println("Progress: " + progress);
        //while (true);

        try {
            System.out.println("I am inside onProgress: " + progress);
            URL url = new URL("http://212.183.159.230/100MB.zip");  // London public test server
            String outputFilename = "test_download.bin";
            Downloader downloader = new Downloader(url, outputFilename);
            downloader.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
