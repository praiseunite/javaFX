package com.aptech.s06.practice;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;

/**
 * PRACTICE 3 (Challenge) — three faults, one class, and none of them throws.
 *
 * This is what a real review feels like. All three faults below produce plausible output and
 * a clean exit; each one is a Sessions 1–5 mistake wearing everyday clothes:
 *
 *   Fault 1 (Session 2) — a value class without equals()/hashCode(), so a Set cannot
 *                         recognise a duplicate.
 *   Fault 2 (Session 3) — a writer that is never closed, so the text never leaves the buffer.
 *   Fault 3 (Session 5) — a shared int incremented from several threads, so updates are lost.
 *
 * The challenge is to say what each one is BEFORE running it. The program prints both
 * answers so you can check yourself, then prints a PASS line for each fix.
 */
public class P3_SpotTheBugs {

    private static final Path SANDBOX = Path.of("sandbox");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(SANDBOX);

        System.out.println("Practice 3 (Challenge) - three faults, one class, nothing thrown");
        System.out.println();

        fault1();
        System.out.println();
        fault2();
        System.out.println();
        fault3();
    }

    /** Fault 1 — identity. */
    private static void fault1() {
        Set<Object> broken = new HashSet<>();
        broken.add(new BrokenStudent("S-1001", "Ada"));
        broken.add(new BrokenStudent("S-1001", "Ada"));

        Set<FaultlessStudent> fixed = new HashSet<>();
        fixed.add(new FaultlessStudent("S-1001", "Ada"));
        fixed.add(new FaultlessStudent("S-1001", "Ada"));

        System.out.println("FAULT 1 - a Set that keeps a duplicate");
        System.out.println("  broken: two copies of S-1001 added, set size = " + broken.size()
                + "   <- identity was compared, not the student id");
        System.out.println("  fixed : two copies of S-1001 added, set size = " + fixed.size()
                + "   PASS - equals() and hashCode() agree on the id");
    }

    /** Fault 2 — a buffer that never reaches the disk. */
    private static void fault2() throws IOException {
        Path leaky = SANDBOX.resolve("p3-leaky.log");
        FileWriter writer = new FileWriter(leaky.toFile());
        writer.write("Ada,S-1001");
        byte[] brokenSize = Files.readAllBytes(leaky);

        Path sound = SANDBOX.resolve("p3-sound.log");
        try (FileWriter ok = new FileWriter(sound.toFile())) {
            ok.write("Ada,S-1001");
        }
        byte[] fixedSize = Files.readAllBytes(sound);

        System.out.println("FAULT 2 - a writer that is never closed");
        System.out.println("  broken: " + brokenSize.length + " bytes on disk after write()"
                + "   <- the text is still in the buffer");
        System.out.println("  fixed : " + fixedSize.length + " bytes on disk after close()"
                + "   PASS - try-with-resources flushed it");
    }

    /** Fault 3 — a lost update. */
    private static void fault3() throws InterruptedException {
        final int threads = 4;
        final int each = 100_000;

        int[] broken = {0};
        CountDownLatch gate = new CountDownLatch(1);
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                await(gate);
                for (int n = 0; n < each; n++) {
                    int current = broken[0];     // READ
                    Thread.yield();              // (demonstration aid: hold the window open)
                    broken[0] = current + 1;     // WRITE - the other workers overwrite it
                }
            });
            workers[i].start();
        }
        gate.countDown();
        for (Thread worker : workers) {
            worker.join();
        }

        LongAdder sound = new LongAdder();
        CountDownLatch gate2 = new CountDownLatch(1);
        Thread[] workers2 = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers2[i] = new Thread(() -> {
                await(gate2);
                for (int n = 0; n < each; n++) {
                    sound.increment();
                }
            });
            workers2[i].start();
        }
        gate2.countDown();
        for (Thread worker : workers2) {
            worker.join();
        }

        long expected = (long) threads * each;

        System.out.println("FAULT 3 - a shared int with count++ spelled out");
        System.out.println("  broken: " + String.format("%,d", broken[0]) + " of "
                + String.format("%,d", expected) + "   <- lost "
                + String.format("%,d", expected - broken[0]) + " updates");
        System.out.println("  fixed : " + String.format("%,d", sound.sum()) + " of "
                + String.format("%,d", expected) + "   PASS - LongAdder lost nothing");
    }

    private static void await(CountDownLatch gate) {
        try {
            gate.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** FAULT 1: a value class that never overrides equals() or hashCode(). */
    static final class BrokenStudent {
        final String id;
        final String name;

        BrokenStudent(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    /** The same class, with the contract honoured. */
    static final class FaultlessStudent {
        final String id;
        final String name;

        FaultlessStudent(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof FaultlessStudent that)) {
                return false;
            }
            return id.equals(that.id);
        }

        @Override public int hashCode() {
            return Objects.hash(id);
        }
    }
}
