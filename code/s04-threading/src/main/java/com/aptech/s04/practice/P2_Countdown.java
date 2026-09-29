package com.aptech.s04.practice;

/** Practice 2 (easy) — a countdown thread; main waits for it with join() before "Lift off!". */
public class P2_Countdown {
    public static void main(String[] args) throws InterruptedException {
        Thread countdown = new Thread(() -> {
            for (int i = 5; i >= 1; i--) {
                System.out.println(i + "...");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println("Countdown aborted!");
                    return;
                }
            }
        }, "countdown");

        countdown.start();
        System.out.println("main: waiting for the countdown");
        countdown.join();
        System.out.println("Lift off!");
    }
}
