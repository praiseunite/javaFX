package com.aptech.s04;

/**
 * Example 7 — Daemon threads: background helpers that do NOT keep the program alive.
 */
public class E07_DaemonThreads {
    public static void main(String[] args) throws InterruptedException {
        Thread autosave = new Thread(() -> {
            int n = 0;
            while (true) {                              // runs "forever"...
                n++;
                System.out.println("  [autosave #" + n + "]");
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }, "autosave");

        autosave.setDaemon(true);                       // MUST be called before start()
        autosave.start();
        System.out.println("autosave is a daemon? " + autosave.isDaemon());

        System.out.println("main: typing a document...");
        Thread.sleep(1000);
        System.out.println("main: finished - the program ends now.");
        // No join and no interrupt: when the last NON-daemon thread (main) ends,
        // the JVM stops and daemon threads are simply switched off.
    }
}
