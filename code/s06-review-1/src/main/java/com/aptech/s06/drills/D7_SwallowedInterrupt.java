package com.aptech.s06.drills;

/**
 * DRILL 7 — the interrupt nobody heard.
 *
 * The bug: a worker is asked to stop with interrupt(), but its catch block swallows the
 * InterruptedException and carries on. Clearing that flag throws away the only signal the
 * thread had, so interrupt() becomes a no-op and the worker runs to the end of its list.
 *
 * Why it matters in real code: this is how Cancel buttons, shutdown hooks and stuck thread
 * pools quietly stop working. Nothing throws, nothing is logged — the stop request is just
 * ignored, and the program hangs around until somebody kills it.
 *
 * The fix: do not swallow it. Either give up what you were doing and return, or restore the
 * flag with Thread.currentThread().interrupt() and let your caller decide what to do next.
 */
public class D7_SwallowedInterrupt {

    private static final int STEPS = 10;
    private static final int STEP_MS = 100;
    private static final int STOP_AFTER_MS = 200;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Drill 7 - the interrupt nobody heard");
        System.out.println("The worker has " + STEPS + " steps of " + STEP_MS + " ms each.");
        System.out.println("After " + STOP_AFTER_MS + " ms we ask it to stop. A worker that listens");
        System.out.println("should give up almost immediately.");
        System.out.println();

        int brokenSteps = run(true);
        int fixedSteps = run(false);

        System.out.println("BROKEN - the catch block swallows the InterruptedException");
        System.out.println("  steps completed in total: " + brokenSteps + " of " + STEPS);
        System.out.println("  -> it ignored the stop request and ran the job to the very end.");
        System.out.println();
        System.out.println("FIXED - the catch block restores the flag and returns");
        System.out.println("  steps completed in total: " + fixedSteps + " of " + STEPS);
        System.out.println("  -> it stopped early, where it was asked to. The exact number of");
        System.out.println("     steps depends on timing; that it stopped early does not.");
    }

    /** Runs the same worker twice: once swallowing interrupts, once respecting them. */
    private static int run(boolean swallow) throws InterruptedException {
        final int[] completed = {0};

        Thread worker = new Thread(() -> {
            for (int step = 1; step <= STEPS; step++) {
                try {
                    Thread.sleep(STEP_MS);
                } catch (InterruptedException e) {
                    if (!swallow) {
                        Thread.currentThread().interrupt();   // FIXED: keep the flag, then leave
                        return;
                    }
                    // BROKEN: swallow it and carry on as though nothing happened
                }
                completed[0] = step;
            }
        }, "worker");

        worker.start();
        Thread.sleep(STOP_AFTER_MS);   // let it get going
        worker.interrupt();            // "please stop"
        worker.join(5000);             // join() gives us a happens-before edge, so the read below is safe
        return completed[0];
    }
}
