package com.aptech.s04.practice;

/** Practice 4 (medium) — a "Loading..." animation thread that main stops with interrupt(). */
public class P4_LoadingSpinner {
    public static void main(String[] args) throws InterruptedException {
        Thread spinner = new Thread(() -> {
            String[] frames = {"|", "/", "-", "\\"};
            int i = 0;
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.println("Loading " + frames[i % frames.length]);
                    i++;
                    Thread.sleep(250);
                }
            } catch (InterruptedException e) {
                // interrupted while sleeping: fall through and finish
            }
            System.out.println("Spinner stopped after " + i + " frames");
        }, "spinner");

        spinner.start();
        Thread.sleep(900);                             // pretend to load something for 0.9 s
        spinner.interrupt();
        spinner.join();
        System.out.println("Loaded!");
    }
}
