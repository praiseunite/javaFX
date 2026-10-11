package com.aptech.s11b.practice;

import com.aptech.s11b.lab.Checks;

import java.util.LinkedList;

/**
 * P6 - Challenge. A third implementation, and one new line of test code: none.
 *
 * The task: write a class that satisfies the list contract in a completely different way - back
 * it with a LinkedList instead of an array - and then run the suite from the lesson against it
 * without changing, adding or weakening a single check.
 *
 * This is the test of whether a suite is worth anything. If your checks were really written
 * against behaviour, a structure they have never seen should walk straight through them. If
 * they were written against the two implementations you happened to have, this exercise will
 * fail, and that is the more useful outcome of the two.
 *
 * Read the last paragraph of the output when you get there. It is the honest part.
 */
public class P6_OneMoreImplementation {

    /**
     * The same promise as IntList, kept by a linked list of nodes rather than by an array.
     * Deliberately naive: every operation is the obvious one, and correctness is the only
     * thing this class is trying to buy.
     */
    static final class LinkedBag implements Checks.IntBag {

        private final LinkedList<Integer> inner = new LinkedList<>();

        @Override
        public void add(int value) {
            inner.addLast(value);
        }

        @Override
        public int get(int index) {
            if (index < 0 || index >= inner.size()) {
                throw new IndexOutOfBoundsException(
                        "Index " + index + " out of bounds for size " + inner.size());
            }
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
            if (index < 0 || index >= inner.size()) {
                throw new IndexOutOfBoundsException(
                        "Index " + index + " out of bounds for size " + inner.size());
            }
            return inner.remove(index);
        }
    }

    public static void main(String[] args) {
        System.out.println("The list contract, run against three implementations. The two below");
        System.out.println("have been tested all lesson; the first one was written five minutes ago");
        System.out.println("and has never been near a check until now.");
        System.out.println();

        Checks checks = new Checks();

        checks.heading("LinkedBag - the one just written, backed by a LinkedList");
        checks.listSuite(LinkedBag::new);
        System.out.println("  " + checks.suiteTally());
        boolean thirdOnePassed = checks.failed() == 0;

        checks.quietPasses();

        checks.heading("IntList - the one written in the lesson");
        checks.listSuite(Checks::handList);
        System.out.println("  " + checks.suiteTally());

        checks.heading("ArrayList - the JDK's");
        checks.listSuite(Checks::jdkList);
        System.out.println("  " + checks.suiteTally());

        System.out.println();
        System.out.println("Totalled: " + checks.tally() + ".");
        System.out.println();

        if (thirdOnePassed) {
            System.out.println("A structure the suite had never seen passed every check without a");
            System.out.println("single new assertion being written. That is the evidence that the");
            System.out.println("checks describe the promise and not the implementations - and it is");
            System.out.println("the same evidence s09's rewrite and s10's tests were built on.");
        } else {
            System.out.println("The new implementation failed. That is a result, not a mistake:");
            System.out.println("either the class is wrong, or a check was written against an");
            System.out.println("implementation instead of against the promise. Read which check");
            System.out.println("broke and decide which of the two it was.");
        }

        System.out.println();
        System.out.println("And now the honest part, which is the reason this exercise exists.");
        System.out.println();
        System.out.println("LinkedBag passed every check, and it is the slow one. Its get(i) walks");
        System.out.println("the list of nodes to reach position i; IntList's get(i) reads one array");
        System.out.println("slot. The suite cannot tell them apart, and it is not being lazy - at");
        System.out.println("the sizes the checks use, and from the outside, they behave identically.");
        System.out.println();
        System.out.println("This is exactly the trap Session 10 warned you about from the other");
        System.out.println("side: a green suite proves the behaviour you thought to check, and");
        System.out.println("nothing else. Correct and fast are two different claims, and only one of");
        System.out.println("them can be proved by a program. The other one you have to reason about -");
        System.out.println("which is what the counts in E01 to E05 were for.");
    }
}
