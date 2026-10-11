package com.aptech.s11b;

import com.aptech.s11b.ds.IntList;

import java.util.Locale;

/**
 * E01 — growing by doubling, seen rather than claimed.
 *
 * You have been told that ArrayList.add() is O(1) on average. This program shows where that
 * "on average" comes from: the backing array is replaced only when it is full, and it is
 * replaced with one twice the size, so the expensive adds get rarer as the list gets longer.
 *
 * Every number here is counted, not timed. A count is the same on every machine and every run.
 */
public class E01_DynamicArrayGrowth {

    public static void main(String[] args) {
        System.out.println("An array cannot get longer. So a list owns an array that is usually");
        System.out.println("bigger than the data, and replaces it when it fills up.");
        System.out.println();

        System.out.println("1. Ten adds, watching the capacity");
        System.out.println("   add   size   capacity   copies so far");
        IntList list = new IntList();
        for (int i = 1; i <= 10; i++) {
            int before = list.capacity();
            list.add(i * 10);
            int after = list.capacity();
            String note = "";
            if (after != before) {
                note = "   <- full: new int[" + after + "], copied " + before + " elements";
            }
            System.out.printf(Locale.ROOT, "   %-5d %-6d %-10d %-8d%s%n",
                    i, list.size(), after, list.copies(), note);
        }

        System.out.println();
        System.out.println("   Ten adds, twelve element copies, and the caller saw none of them.");
        System.out.println("   add(50) did not just append - it allocated an array twice the size,");
        System.out.println("   copied four elements into it, and threw the old array away.");
        System.out.println();

        System.out.println("2. The same count for bigger lists");
        System.out.println("   adds     capacity   total copies   copies per add");
        int[] sizes = {10, 100, 1000, 10000};
        for (int n : sizes) {
            IntList big = new IntList();
            for (int i = 0; i < n; i++) {
                big.add(i);
            }
            System.out.printf(Locale.ROOT, "   %-8d %-10d %-14d %.2f%n",
                    n, big.capacity(), big.copies(), (double) big.copies() / n);
        }

        System.out.println();
        System.out.println("   However large the list, the copies per add stay between 1 and 2 -");
        System.out.println("   and they bounce around inside that band, because how much of the");
        System.out.println("   last array was in use when it filled depends on where n falls.");
        System.out.println("   Growth copies n, then n/2, then n/4 and so on, and that sum is");
        System.out.println("   always less than 2n. That is the entire content of the sentence");
        System.out.println("   \"add() is O(1)\": not every add, the average one.");
        System.out.println();
        System.out.println("3. Why doubling, and not growing by one slot");
        System.out.println("   Growing by one slot per add means the i-th add copies i-1 elements,");
        System.out.println("   so n adds copy 1 + 2 + ... + (n-1). Counted the same way as above:");
        System.out.println("   adds     grow by one    grow by doubling");
        long growByOne = 0;
        int nextReport = 1;
        for (int n = 1; n <= 10000; n++) {
            growByOne += n - 1;                       // the copy this add would cost
            if (n == nextReport) {
                System.out.printf(Locale.ROOT, "   %-8d %-15d %d%n", n, growByOne, copiesFor(n));
                nextReport = (n == 1) ? 10 : nextReport * 10;
            }
        }
        System.out.println();
        System.out.println("   Ten thousand adds: forty-nine million copies against sixteen thousand.");
        System.out.println("   Both lists hold the same thing. One of them is doing the work anyway.");
        System.out.println();
        System.out.println("   ArrayList makes the same promise we just measured, but no more: its");
        System.out.println("   javadoc calls adding an element O(1) *amortized*, and says the growth");
        System.out.println("   policy itself is unspecified. Amortized is a promise about the average,");
        System.out.println("   which is exactly what section 2 measured - so rely on the average and");
        System.out.println("   never on the size of the next array.");
    }

    /** Total element copies the doubling list performs while growing to n elements. */
    private static long copiesFor(int n) {
        IntList list = new IntList();
        for (int i = 0; i < n; i++) {
            list.add(i);
        }
        return list.copies();
    }
}
