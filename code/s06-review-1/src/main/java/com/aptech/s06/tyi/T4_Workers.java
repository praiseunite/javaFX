package com.aptech.s06.tyi;

/**
 * TRY IT YOURSELF 4 — Session 4: Threading.
 *
 * Start three named workers that each take a different amount of time, ask whether each one
 * is still alive, then wait for all of them. The four things being tested:
 *
 *   - a thread is created by extending Thread OR by handing a Runnable to a Thread
 *     (the lambda version, used here, is the modern one);
 *   - start() runs the job on a NEW thread; run() would just call a method and do the work
 *     on the calling thread — same output, no concurrency, and it is the classic mistake;
 *   - isAlive() is false before start() and false after the thread finishes, so it only
 *     means "running right now";
 *   - join() is what stops main printing its report before the workers have done anything.
 */
public class T4_Workers {

    public static void main(String[] args) throws InterruptedException {
        String[] jobs = {"roster", "timetable", "fees"};
        Thread[] workers = new Thread[jobs.length];

        for (int i = 0; i < jobs.length; i++) {
            String job = jobs[i];
            int workMs = 200 * (i + 1);      // roster 200 ms, timetable 400 ms, fees 600 ms

            workers[i] = new Thread(() -> {
                sleep(workMs);
                System.out.println("   finished: " + job
                        + "   (on thread " + Thread.currentThread().getName() + ")");
            }, "worker-" + job);
        }

        System.out.println("Try It Yourself 4 - three named workers");
        System.out.println("before start(), alive = " + countAlive(workers) + " of " + workers.length
                + "   (a thread that has not started is not alive)");

        for (Thread worker : workers) {
            worker.start();
        }
        System.out.println("straight after start(), alive = " + countAlive(workers)
                + " of " + workers.length);

        long start = System.currentTimeMillis();
        for (Thread worker : workers) {
            worker.join();                  // wait here until this one has finished
        }
        long took = System.currentTimeMillis() - start;

        System.out.println("after join(), alive = " + countAlive(workers) + " of " + workers.length);
        System.out.println();
        System.out.println("All three ran at the same time: the whole batch took about " + took
                + " ms,");
        System.out.println("which is the time of the SLOWEST job (600 ms), not the sum of all three.");
    }

    /** How many of these threads are running right now. */
    private static int countAlive(Thread[] threads) {
        int alive = 0;
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                alive++;
            }
        }
        return alive;
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
