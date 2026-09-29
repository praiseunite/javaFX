package com.aptech.s04.lab;

import java.util.ArrayList;
import java.util.List;

/**
 * GUIDED LAB — The Busy Kitchen: several chefs cook at the same time, the waiter waits for all of them,
 * and a background clock (daemon) announces the time.
 */
public class Kitchen {

    /** STEP 1 — the JOB of one chef: prepare a dish in a number of steps. */
    static class Chef implements Runnable {
        private final String dish;
        private final int steps;
        private final int msPerStep;

        Chef(String dish, int steps, int msPerStep) {
            this.dish = dish;
            this.steps = steps;
            this.msPerStep = msPerStep;
        }

        @Override
        public void run() {
            String me = Thread.currentThread().getName();
            for (int i = 1; i <= steps; i++) {
                try {
                    Thread.sleep(msPerStep);                  // STEP 2 — cooking takes time
                } catch (InterruptedException e) {
                    System.out.println(me + " was stopped!");
                    return;
                }
                System.out.println("  " + me + ": " + dish + " step " + i + "/" + steps);
            }
            System.out.println("  " + me + ": " + dish + " is READY");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // STEP 5 — a daemon clock that announces the time every 250 ms
        Thread clock = new Thread(() -> {
            int ms = 0;
            while (true) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    return;
                }
                ms += 250;
                System.out.println("[clock] " + ms + " ms");
            }
        }, "clock");
        clock.setDaemon(true);
        clock.start();

        // STEP 3 — three chefs, three threads, working at the same time
        List<Thread> chefs = new ArrayList<>();
        chefs.add(new Thread(new Chef("Jollof rice", 3, 300), "Chef Ada"));
        chefs.add(new Thread(new Chef("Pepper soup", 2, 400), "Chef Ben"));
        chefs.add(new Thread(new Chef("Puff-puff", 4, 150), "Chef Chi"));

        long start = System.currentTimeMillis();
        for (Thread chef : chefs) {
            chef.start();
        }

        // STEP 4 — the waiter waits until EVERY chef has finished
        for (Thread chef : chefs) {
            chef.join();
        }
        long took = System.currentTimeMillis() - start;
        System.out.println("Waiter: all dishes ready - dinner is served! (about " + (took / 100 * 100) + " ms)");
        System.out.println("Cooking one after another would take about " + (3 * 300 + 2 * 400 + 4 * 150) + " ms");
    }
}
