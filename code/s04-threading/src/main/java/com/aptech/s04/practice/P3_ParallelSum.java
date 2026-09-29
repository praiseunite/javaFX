package com.aptech.s04.practice;

/** Practice 3 (medium) — add up 1..1,000,000 with 4 threads; each thread sums its own quarter. */
public class P3_ParallelSum {
    public static void main(String[] args) throws InterruptedException {
        int n = 1_000_000;
        int parts = 4;
        long[] partial = new long[parts];              // each thread writes ONLY its own slot
        Thread[] workers = new Thread[parts];

        for (int p = 0; p < parts; p++) {
            final int part = p;                        // lambdas need a final (unchanging) variable
            int from = part * (n / parts) + 1;
            int to = (part + 1) * (n / parts);
            workers[p] = new Thread(() -> {
                long sum = 0;
                for (int i = from; i <= to; i++) {
                    sum += i;
                }
                partial[part] = sum;
            }, "adder-" + p);
            workers[p].start();
        }

        long total = 0;
        for (int p = 0; p < parts; p++) {
            workers[p].join();                         // wait, THEN read that thread's result
            System.out.println(workers[p].getName() + " -> " + partial[p]);
            total += partial[p];
        }
        System.out.println("Total:    " + total);
        System.out.println("Formula:  " + (long) n * (n + 1) / 2);
    }
}
