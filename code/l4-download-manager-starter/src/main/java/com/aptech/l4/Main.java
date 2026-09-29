package com.aptech.l4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * L4 — a small demo that uses YOUR DownloadManager. GIVEN complete.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<String> log = Collections.synchronizedList(new ArrayList<>());
        List<Thread> downloads = new ArrayList<>();
        downloads.add(DownloadManager.createDownload("photo.jpg", 3, 200, log));
        downloads.add(DownloadManager.createDownload("video.mp4", 6, 200, log));
        downloads.add(DownloadManager.createDownload("notes.pdf", 2, 200, log));

        Thread reporter = DownloadManager.startReporter(
                () -> System.out.println("[status] " + DownloadManager.countAlive(downloads) + " downloads running, "
                        + log.size() + " chunks done"), 300);

        DownloadManager.startAll(downloads);
        Thread.sleep(700);
        Thread video = downloads.get(1);
        System.out.println("User cancels video.mp4 -> stopped? " + DownloadManager.cancel(video, 1000));

        DownloadManager.waitForAll(downloads);
        reporter.interrupt();
        System.out.println("All done. Log:");
        synchronized (log) {
            log.forEach(line -> System.out.println("  " + line));
        }
    }
}
