package com.aptech.s05;

/**
 * Multithreading is one program doing several jobs at the same time.
 * The two methods you need first are isAlive() and join().
 *
 * One chef cooks one dish after another (single-threaded), then three chefs
 * cook at the same time (multithreaded) - and the clock proves the difference.
 */
public class E01_Multithreading {

    /** The JOB one chef does: plate a dish in a number of steps. */
    static class Cook implements Runnable {
        private final String dish;
        private final int steps;

        Cook(String dish, int steps) {
            this.dish = dish;
            this.steps = steps;
        }

        @Override
        public void run() {
            for (int i = 1; i <= steps; i++) {
                sleep(200);
                System.out.println("    " + Thread.currentThread().getName()
                        + " -> " + dish + " step " + i + "/" + steps);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("A) ONE chef, one dish after another (this is a single thread):");
        long start = System.currentTimeMillis();
        new Cook("Jollof rice", 3).run();          // run() = this thread does the work now
        new Cook("Puff-puff", 2).run();            // and only then the next dish starts
        long oneChef = System.currentTimeMillis() - start;
        System.out.println("    Took " + oneChef + " ms. The two jobs never overlapped.\n");

        System.out.println("B) THREE chefs at the same time (this is multithreading):");
        Thread ada = new Thread(new Cook("Jollof rice", 3), "Chef Ada");
        Thread ben = new Thread(new Cook("Pepper soup", 3), "Chef Ben");
        Thread chi = new Thread(new Cook("Puff-puff", 3), "Chef Chi");

        start = System.currentTimeMillis();
        ada.start();                                // start() = "begin this job on a NEW thread"
        ben.start();
        chi.start();

        // isAlive() asks: is this thread still running?
        System.out.println("    Just after start(): ada.isAlive() = " + ada.isAlive());

        ada.join();                                 // join() = "wait here until Ada has finished"
        System.out.println("    After ada.join():  ada.isAlive() = " + ada.isAlive());
        ben.join();
        chi.join();

        long threeChefs = System.currentTimeMillis() - start;
        System.out.println("    Took " + threeChefs + " ms for the same 9 steps.");
        System.out.printf("    Doing it one after another would take about %d ms (%.1fx slower).%n",
                9 * 200, (9 * 200) / (double) threeChefs);
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();      // never swallow an interrupt
        }
    }
}
