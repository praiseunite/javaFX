package com.aptech.s11b;

import com.aptech.s11b.ds.IntList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * E02 — measuring cost by counting, not by timing.
 *
 * A millisecond reading is noise plus truth: the JIT, the machine and whatever else is running
 * all get a vote. A count of array reads is the same on every machine and every run, and it
 * shows the shape of the growth, which is what "O(1)" and "O(n)" actually describe.
 *
 * Both structures count their own array reads. IntList counts inside get(); for the JDK's list
 * we subclass ArrayList and override get() to count too, so the two numbers mean the same thing.
 * The search loop is written once per structure and is otherwise identical.
 */
public class E02_MeasuringCost {

    /** An ArrayList that counts its own get() calls, so both sides measure the same thing. */
    @SuppressWarnings("serial")
    static final class CountingList extends ArrayList<Integer> {
        private long reads;

        long reads() {
            return reads;
        }

        @Override
        public Integer get(int index) {
            reads++;
            return super.get(index);
        }
    }

    public static void main(String[] args) {
        System.out.println("Two lists, one loop. We count array reads, never milliseconds.");
        System.out.println();

        System.out.println("1. Reading three known positions");
        System.out.println("   size       IntList: 3 gets   ArrayList: 3 gets");
        for (int n : new int[]{10, 1000, 100000}) {
            IntList mine = fill(n);
            CountingList jdk = fillJdk(n);
            System.out.printf(Locale.ROOT, "   %-10d %-20d %d%n",
                    n, threeGets(mine), threeGets(jdk));
        }
        System.out.println("   Three reads in each, and the size never enters into it. That is O(1):");
        System.out.println("   the work does not grow when the list does. IntList gets it from one");
        System.out.println("   line - return data[index]; - and ArrayList from the same line.");
        System.out.println();

        System.out.println("2. Scanning for a value - the same loop over each structure");
        System.out.println("   size       IntList first   IntList last   IntList absent   ArrayList last");
        for (int n : new int[]{10, 1000, 100000}) {
            IntList mine = fill(n);
            CountingList jdk = fillJdk(n);
            System.out.printf(Locale.ROOT, "   %-10d %-16d %-15d %-17d %d%n",
                    n, scanIntList(mine, 0), scanIntList(mine, n - 1),
                    scanIntList(mine, -1), scanArrayList(jdk, n - 1));
        }
        System.out.println("   Finding the last element costs n reads; finding a value that is not");
        System.out.println("   there costs n as well. The hand-written list and the JDK's agree to");
        System.out.println("   the read, because the algorithm is the same. What would change the");
        System.out.println("   cost is a different algorithm, not a different class.");
        System.out.println();

        System.out.println("3. Three numbers worth remembering");
        System.out.println("   get(i)           1 read, whatever n is          O(1)");
        System.out.println("   scan for x       up to n reads                    O(n)");
        System.out.println("   add(x)           usually 1 write, occasionally");
        System.out.println("                    a grow that copies everything      O(1) amortised");
        System.out.println();
        System.out.println("   The third line is E01. The interesting question is whether a LOOKUP can");
        System.out.println("   ever beat a scan - and it can. That is E03.");
    }

    private static IntList fill(int n) {
        IntList list = new IntList();
        for (int i = 0; i < n; i++) {
            list.add(i);
        }
        return list;
    }

    private static CountingList fillJdk(int n) {
        CountingList list = new CountingList();
        for (int i = 0; i < n; i++) {
            list.add(i);
        }
        return list;
    }

    private static long threeGets(IntList list) {
        long before = list.reads();
        list.get(0);
        list.get(list.size() / 2);
        list.get(list.size() - 1);
        return list.reads() - before;
    }

    private static long threeGets(CountingList list) {
        long before = list.reads();
        list.get(0);
        list.get(list.size() / 2);
        list.get(list.size() - 1);
        return list.reads() - before;
    }

    private static long scanIntList(IntList list, int target) {
        long before = list.reads();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == target) {
                break;
            }
        }
        return list.reads() - before;
    }

    private static long scanArrayList(CountingList list, int target) {
        long before = list.reads();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(target)) {
                break;
            }
        }
        return list.reads() - before;
    }
}
