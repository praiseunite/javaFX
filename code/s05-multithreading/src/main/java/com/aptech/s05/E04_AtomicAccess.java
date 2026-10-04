package com.aptech.s05;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.IntSupplier;

/**
 * Atomic access: the race condition is gone without writing a single
 * synchronized keyword.
 *
 * An atomic class does its read-modify-write as ONE indivisible step, so no
 * update can be lost. LongAdder is the high-throughput version: when many
 * threads hammer it, it spreads the work over several cells instead of making
 * everyone fight over one value.
 *
 * Compare this run with E02_RaceCondition - same four chefs, same 400,000 orders.
 */
public class E04_AtomicAccess {

    static final int CHEFS = 4;
    static final int ORDERS_EACH = 100_000;

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();
        System.out.println("AtomicInteger.incrementAndGet() -> "
                + race(counter::incrementAndGet, counter::get) + "   (expected 400000)");

        LongAdder adder = new LongAdder();
        System.out.println("LongAdder.increment()           -> "
                + race(adder::increment, adder::intValue) + "   (expected 400000)");

        System.out.println();
        System.out.println("Use an atomic class when ONE variable is all you are protecting.");
        System.out.println("Use synchronized when several variables must change together and");
        System.out.println("a half-finished change must never be visible to another thread.");
    }

    /** Runs addOne on four threads at once and returns the final total. */
    static int race(Runnable addOne, IntSupplier total) throws InterruptedException {
        Thread[] chefs = new Thread[CHEFS];
        for (int i = 0; i < CHEFS; i++) {
            chefs[i] = new Thread(() -> {
                for (int n = 0; n < ORDERS_EACH; n++) {
                    addOne.run();
                }
            }, "Chef " + (char) ('A' + i));
            chefs[i].start();
        }
        for (Thread chef : chefs) {
            chef.join();
        }
        return total.getAsInt();
    }
}
