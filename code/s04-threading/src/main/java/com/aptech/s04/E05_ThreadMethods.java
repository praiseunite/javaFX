package com.aptech.s04;

/**
 * Example 5 — The most-used Thread methods: start, sleep, join, isAlive, getName/setName, priority.
 */
public class E05_ThreadMethods {
    public static void main(String[] args) throws InterruptedException {
        Thread download = new Thread(() -> {
            for (int percent = 25; percent <= 100; percent += 25) {
                try {
                    Thread.sleep(150);                  // pretend to download a piece
                } catch (InterruptedException e) {
                    return;
                }
                System.out.println("  " + Thread.currentThread().getName() + " " + percent + "%");
            }
        });

        download.setName("downloader");
        download.setPriority(Thread.MAX_PRIORITY);      // a HINT to the scheduler (10), not a guarantee
        System.out.println("Name: " + download.getName() + ", priority: " + download.getPriority());
        System.out.println("Alive before start()? " + download.isAlive());

        download.start();
        System.out.println("Alive after start()?  " + download.isAlive());

        download.join();                                // main waits here until the download is done
        System.out.println("Alive after join()?   " + download.isAlive());

        // join(ms) waits at most that long
        Thread slow = new Thread(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException e) { /* stopped */ }
        });
        slow.start();
        slow.join(300);                                 // give up waiting after 300 ms
        System.out.println("Still running after join(300)? " + slow.isAlive());
        slow.interrupt();                               // tidy up: ask it to stop
        slow.join();
    }
}
