package com.aptech.s11b.lab;

import com.aptech.s11b.ds.IntList;
import com.aptech.s11b.ds.RingBuffer;
import com.aptech.s11b.ds.SimpleMap;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * One suite, two implementations.
 *
 * The third time this course has done this, and the point is the same every time: a check is
 * only worth anything if it is written against the <em>behaviour</em>, not against the class.
 * Nothing in the suite below knows whether it is talking to the structure you wrote this morning
 * or to the one the JDK has shipped for thirty years. It knows three small interfaces and that
 * is all.
 *
 * So each family is run twice - {@link IntBag} against {@code IntList} and against
 * {@code ArrayList}, {@link StringIntMap} against {@code SimpleMap} and against {@code HashMap},
 * {@link StringDeque} against {@code RingBuffer} and against {@code ArrayDeque}. Every check
 * prints PASS or FAIL, and when the same check passes on both sides, the two structures agree on
 * what they promise. That agreement, not the code, is what "correct" means.
 *
 * The adapters do a little translation, and the translation is worth reading: in a Deque,
 * {@code push} and {@code pop} both work on the <em>front</em>, so {@code RingBuffer.push} maps to
 * {@code addLast} and {@code RingBuffer.pop} to {@code pollLast}. Same ends, different names.
 *
 * Run it through {@code E06_OneSuiteTwoImplementations}, which prints the headings and then the
 * places where the two sides honestly differ.
 */
public class Checks {

    // ---------------------------------------------------------------- the three shapes

    /** What a growable list of ints must do. Nothing about how. */
    public interface IntBag {
        void add(int value);

        int get(int index);

        int size();

        boolean contains(int value);

        int removeAt(int index);
    }

    /** What a map from String to int must do. */
    public interface StringIntMap {
        void put(String key, int value);

        Integer get(String key);

        boolean containsKey(String key);

        int size();
    }

    /** What a buffer you can take from either end must do. */
    public interface StringDeque {
        void push(String value);

        /** Removes the newest - the stack end. */
        String pop();

        /** Removes the oldest - the queue end. */
        String poll();

        String peekFront();

        String peekBack();

        int size();

        boolean isEmpty();
    }

    // ---------------------------------------------------------------- the hand-built side

    // ---------------------------------------------------------------- who is behind the interface

    /**
     * The six factories below are the only place in the suite where a real class is named. The
     * checks receive the interface, so the side that is being tested cannot change how they read.
     * Passing {@code Checks::handList} and {@code Checks::jdkList} to the same method is the
     * whole idea of this file in one line.
     */
    public static IntBag handList() {
        return new HandList();
    }

    public static IntBag jdkList() {
        return new JdkList();
    }

    public static StringIntMap handMap() {
        return new HandMap();
    }

    public static StringIntMap jdkMap() {
        return new JdkMap();
    }

    public static StringDeque handDeque() {
        return new HandDeque();
    }

    public static StringDeque jdkDeque() {
        return new JdkDeque();
    }

    private static final class HandList implements IntBag {
        private final IntList inner = new IntList();

        @Override
        public void add(int value) {
            inner.add(value);
        }

        @Override
        public int get(int index) {
            return inner.get(index);
        }

        @Override
        public int size() {
            return inner.size();
        }

        @Override
        public boolean contains(int value) {
            return inner.contains(value);
        }

        @Override
        public int removeAt(int index) {
            return inner.removeAt(index);
        }
    }

    private static final class HandMap implements StringIntMap {
        private final SimpleMap inner = new SimpleMap();

        @Override
        public void put(String key, int value) {
            inner.put(key, value);
        }

        @Override
        public Integer get(String key) {
            return inner.get(key);
        }

        @Override
        public boolean containsKey(String key) {
            return inner.containsKey(key);
        }

        @Override
        public int size() {
            return inner.size();
        }
    }

    private static final class HandDeque implements StringDeque {
        private final RingBuffer inner = new RingBuffer(8);

        @Override
        public void push(String value) {
            inner.push(value);
        }

        @Override
        public String pop() {
            return inner.pop();
        }

        @Override
        public String poll() {
            return inner.poll();
        }

        @Override
        public String peekFront() {
            return inner.peekFront();
        }

        @Override
        public String peekBack() {
            return inner.peekBack();
        }

        @Override
        public int size() {
            return inner.size();
        }

        @Override
        public boolean isEmpty() {
            return inner.isEmpty();
        }
    }

    // ---------------------------------------------------------------- the JDK side

    private static final class JdkList implements IntBag {
        private final ArrayList<Integer> inner = new ArrayList<>();

        @Override
        public void add(int value) {
            inner.add(value);
        }

        @Override
        public int get(int index) {
            return inner.get(index);
        }

        @Override
        public int size() {
            return inner.size();
        }

        @Override
        public boolean contains(int value) {
            return inner.contains(value);
        }

        @Override
        public int removeAt(int index) {
            return inner.remove(index);
        }
    }

    private static final class JdkMap implements StringIntMap {
        private final HashMap<String, Integer> inner = new HashMap<>();

        @Override
        public void put(String key, int value) {
            inner.put(key, value);
        }

        @Override
        public Integer get(String key) {
            return inner.get(key);
        }

        @Override
        public boolean containsKey(String key) {
            return inner.containsKey(key);
        }

        @Override
        public int size() {
            return inner.size();
        }
    }

    private static final class JdkDeque implements StringDeque {
        private final ArrayDeque<String> inner = new ArrayDeque<>();

        /** Deque.push is addFirst; ours pushes at the back, so this is addLast. */
        @Override
        public void push(String value) {
            inner.addLast(value);
        }

        /** And Deque.pop is removeFirst; ours pops the back. */
        @Override
        public String pop() {
            return inner.pollLast();
        }

        /** Here the two agree exactly: poll is the oldest element. */
        @Override
        public String poll() {
            return inner.pollFirst();
        }

        @Override
        public String peekFront() {
            return inner.peekFirst();
        }

        @Override
        public String peekBack() {
            return inner.peekLast();
        }

        @Override
        public int size() {
            return inner.size();
        }

        @Override
        public boolean isEmpty() {
            return inner.isEmpty();
        }
    }

    // ---------------------------------------------------------------- the counters

    private int passed;
    private int failed;
    private int suitePassed;
    private int suiteFailed;
    private boolean quiet;

    /**
     * After this, only failures are printed.
     *
     * The second implementation to be tested is the JDK's, and printing its thirty-odd PASS lines
     * would double the length of the report to say "the same thing again". The checks still run
     * and still count - silence is a reporting choice, not agreement.
     */
    public void quietPasses() {
        quiet = true;
    }

    /** Every suite method starts here, so a per-suite tally is possible. */
    private void startSuite() {
        suitePassed = 0;
        suiteFailed = 0;
    }

    /** How the suite that just ran did, on its own. */
    public String suiteTally() {
        int total = suitePassed + suiteFailed;
        return total + (total == 1 ? " check, " : " checks, ")
                + suitePassed + " passed, " + suiteFailed + " failed";
    }

    /** How every suite so far did, together. */
    public String tally() {
        int total = passed + failed;
        return total + (total == 1 ? " check, " : " checks, ")
                + passed + " passed, " + failed + " failed";
    }

    public int failed() {
        return failed;
    }

    /** One check, printed as it happens. A suite nobody can read is a suite nobody runs. */
    public void check(String name, boolean ok) {
        if (ok) {
            passed++;
            suitePassed++;
            if (!quiet) {
                System.out.printf("  PASS  %s%n", name);
            }
        } else {
            failed++;
            suiteFailed++;
            System.out.printf("  FAIL  %s   (wrong answer)%n", name);
        }
    }

    public void checkEquals(String name, Object expected, Object actual) {
        if (Objects.equals(expected, actual)) {
            passed++;
            suitePassed++;
            if (!quiet) {
                System.out.printf("  PASS  %s%n", name);
            }
        } else {
            failed++;
            suiteFailed++;
            System.out.printf("  FAIL  %s   (expected %s, got %s)%n", name, expected, actual);
        }
    }

    /**
     * Runs {@code body} and checks that it threw. The name of the exception that actually came
     * out is printed either way, because "it threw" is not the interesting part - which one is.
     */
    public void checkThrows(String name, Class<? extends Throwable> expected, Runnable body) {
        try {
            body.run();
            failed++;
            suiteFailed++;
            System.out.printf("  FAIL  %s   (nothing was thrown, expected %s)%n",
                    name, expected.getSimpleName());
        } catch (Throwable thrown) {
            if (expected.isInstance(thrown)) {
                passed++;
                suitePassed++;
                if (!quiet) {
                    System.out.printf("  PASS  %s   (threw %s)%n",
                            name, thrown.getClass().getSimpleName());
                }
            } else {
                failed++;
                suiteFailed++;
                System.out.printf("  FAIL  %s   (expected %s, threw %s)%n",
                        name, expected.getSimpleName(), thrown.getClass().getSimpleName());
            }
        }
    }

    /** A blank line and a heading, so the suite reads as one report rather than a wall of checks. */
    public void heading(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }

    // ---------------------------------------------------------------- the suites

    /**
     * The list contract. Every check builds its own list, so no check depends on another having
     * run first - a suite that only passes in order is a suite that hides bugs.
     */
    public void listSuite(Supplier<IntBag> factory) {
        startSuite();
        IntBag list = factory.get();
        check("a new list is empty", list.size() == 0);

        list = factory.get();
        list.add(42);
        checkEquals("add then get returns what was added", 42, list.get(0));
        checkEquals("and the size followed", 1, list.size());

        list = factory.get();
        for (int i = 1; i <= 3; i++) {
            list.add(i);
        }
        checkEquals("order is the order you added", "1,2,3", join(list));

        list = factory.get();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        checkEquals("a hundred adds, size 100", 100, list.size());
        checkEquals("the first is still first", 0, list.get(0));
        checkEquals("the last is still last", 99, list.get(99));
        checkEquals("and every element survived the growth", 4950, sum(list));

        list = factory.get();
        for (int i = 1; i <= 3; i++) {
            list.add(i);
        }
        checkEquals("removeAt returns the element it removed", 1, list.removeAt(0));
        checkEquals("removeAt shifts the rest left", "2,3", join(list));
        checkEquals("removeAt shrinks the size", 2, list.size());

        list = factory.get();
        for (int i = 1; i <= 3; i++) {
            list.add(i);
        }
        check("contains finds a stored element", list.contains(2));
        check("contains does not find an absent one", !list.contains(99));

        IntBag empty = factory.get();
        checkThrows("get(0) on an empty list throws IndexOutOfBoundsException",
                IndexOutOfBoundsException.class, () -> empty.get(0));
        IntBag three = factory.get();
        for (int i = 1; i <= 3; i++) {
            three.add(i);
        }
        checkThrows("get(-1) throws IndexOutOfBoundsException",
                IndexOutOfBoundsException.class, () -> three.get(-1));
        checkThrows("get(3) on a list of three throws IndexOutOfBoundsException",
                IndexOutOfBoundsException.class, () -> three.get(3));
    }

    /** The map contract: put, get, replace, and the equals-not-identity rule. */
    public void mapSuite(Supplier<StringIntMap> factory) {
        startSuite();
        StringIntMap map = factory.get();
        check("a new map is empty", map.size() == 0);

        map = factory.get();
        map.put("BOLT", 250);
        checkEquals("put then get returns the value", 250, map.get("BOLT"));
        checkEquals("an absent key reads as null", null, map.get("NUT"));
        check("containsKey is true for a key that is there", map.containsKey("BOLT"));
        check("containsKey is false for one that is not", !map.containsKey("NUT"));

        map = factory.get();
        map.put("BOLT", 250);
        map.put("BOLT", 999);
        checkEquals("putting the same key twice replaces the value", 999, map.get("BOLT"));
        checkEquals("and does not store it twice", 1, map.size());

        map = factory.get();
        for (int i = 0; i < 200; i++) {
            map.put("k" + i, i);
        }
        checkEquals("two hundred keys, size 200", 200, map.size());
        checkEquals("the first one is still reachable", 0, map.get("k0"));
        checkEquals("so is the last", 199, map.get("k199"));
        int found = 0;
        for (int i = 0; i < 200; i++) {
            if (Integer.valueOf(i).equals(map.get("k" + i))) {
                found++;
            }
        }
        checkEquals("and all two hundred read back correctly", 200, found);

        map = factory.get();
        map.put("Ada", 1);
        String separateCopy = new StringBuilder("A").append("da").toString();
        check("a key that is equal but not the same object is found",
                Integer.valueOf(1).equals(map.get(separateCopy)));
    }

    /** The deque contract: two ends, one array. */
    public void dequeSuite(Supplier<StringDeque> factory) {
        startSuite();
        StringDeque deque = factory.get();
        check("a new buffer is empty", deque.isEmpty() && deque.size() == 0);

        deque = factory.get();
        deque.push("a");
        checkEquals("push then pop gives it straight back", "a", deque.pop());
        check("and that leaves it empty", deque.isEmpty());

        deque = factory.get();
        deque.push("a");
        deque.push("b");
        deque.push("c");
        checkEquals("pop takes the newest - a stack", "c", deque.pop());
        checkEquals("then the next newest", "b", deque.pop());
        checkEquals("and size counts down", 1, deque.size());

        deque = factory.get();
        deque.push("a");
        deque.push("b");
        deque.push("c");
        checkEquals("poll takes the oldest - a queue", "a", deque.poll());
        checkEquals("then the next oldest", "b", deque.poll());

        deque = factory.get();
        deque.push("first");
        deque.push("last");
        checkEquals("peekFront shows the oldest", "first", deque.peekFront());
        checkEquals("peekBack shows the newest", "last", deque.peekBack());
        checkEquals("and peeking removed nothing", 2, deque.size());

        deque = factory.get();
        boolean turnsKeptOrder = true;
        for (int i = 0; i < 50; i++) {
            deque.push("x" + i);
            if (!("x" + i).equals(deque.poll())) {
                turnsKeptOrder = false;
            }
        }
        check("fifty push/poll turns each come back in order", turnsKeptOrder);
        check("and the buffer is empty afterwards", deque.isEmpty());

        deque = factory.get();
        for (int i = 0; i < 8; i++) {
            deque.push("v" + i);
        }
        checkEquals("it fills to its capacity", 8, deque.size());

        deque = factory.get();
        for (int i = 0; i < 5; i++) {
            deque.push("gone" + i);
            deque.poll();
        }
        for (int i = 0; i < 8; i++) {
            deque.push("w" + i);
        }
        boolean orderAfterWrap = true;
        for (int i = 0; i < 8; i++) {
            if (!("w" + i).equals(deque.poll())) {
                orderAfterWrap = false;
            }
        }
        check("eight more, read back in order after the indexes wrapped", orderAfterWrap);
    }

    // ---------------------------------------------------------------- helpers

    private static String join(IntBag list) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (text.length() > 0) {
                text.append(',');
            }
            text.append(list.get(i));
        }
        return text.toString();
    }

    private static int sum(IntBag list) {
        int total = 0;
        for (int i = 0; i < list.size(); i++) {
            total += list.get(i);
        }
        return total;
    }
}
