package com.aptech.s04.practice;

/** Practice 5 (challenge) — a relay race: each runner's thread join()s the previous runner before starting. */
public class P5_RelayRace {

    static Thread runner(String name, Thread previous) {
        return new Thread(() -> {
            try {
                if (previous != null) {
                    previous.join();                   // wait for the baton
                }
                System.out.println(name + " takes the baton and runs");
                Thread.sleep(150);
                System.out.println(name + " finishes the leg");
            } catch (InterruptedException e) {
                System.out.println(name + " was stopped");
            }
        }, name);
    }

    public static void main(String[] args) throws InterruptedException {
        Thread r1 = runner("Runner 1", null);
        Thread r2 = runner("Runner 2", r1);
        Thread r3 = runner("Runner 3", r2);
        Thread r4 = runner("Runner 4", r3);

        // Start them in the WRONG order on purpose - join() still keeps the race in order
        r4.start();
        r3.start();
        r2.start();
        r1.start();

        r4.join();
        System.out.println("Race complete!");
    }
}
