package com.aptech.s04;

/**
 * Example 6 — Managing threads: how to STOP a thread politely with interrupt().
 * (Java has no safe "kill" button — the thread must agree to stop.)
 */
public class E06_StoppingThreads {
    public static void main(String[] args) throws InterruptedException {
        Thread clock = new Thread(() -> {
            int tick = 0;
            while (!Thread.currentThread().isInterrupted()) {   // keep going until asked to stop
                tick++;
                System.out.println("tick " + tick);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println("interrupted while sleeping - cleaning up");
                    return;                                      // leave run(), so the thread ends
                }
            }
        }, "clock");

        clock.start();
        Thread.sleep(500);                  // let it tick for about half a second
        System.out.println("main: please stop, clock");
        clock.interrupt();                  // politely ask the thread to stop
        clock.join();
        System.out.println("clock state: " + clock.getState());
    }
}
