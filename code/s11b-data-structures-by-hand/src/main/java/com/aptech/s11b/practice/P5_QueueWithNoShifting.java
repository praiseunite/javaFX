package com.aptech.s11b.practice;

import com.aptech.s11b.ds.RingBuffer;

import java.util.Locale;

/**
 * P5 - Medium. Why a queue is not a list.
 *
 * The task: process a thousand jobs, keeping at most sixteen waiting at a time, and count the
 * element moves each of two designs performs. One is a ring buffer; the other is a list that
 * takes its oldest item from the front by shifting everything else down.
 *
 * Both will produce exactly the same answers. Only one of them will have moved a single element,
 * and the point of the exercise is to see the size of the gap rather than be told about it.
 */
public class P5_QueueWithNoShifting {

    private static final int JOBS = 1000;
    private static final int WINDOW = 16;

    public static void main(String[] args) {
        System.out.println("A thousand jobs arrive. At most " + WINDOW + " are ever waiting at once,");
        System.out.println("and every job must come out in the order it went in. Then how much");
        System.out.println("shuffling did the container do to make that true?");
        System.out.println();

        RingBuffer ring = new RingBuffer(WINDOW);
        ShiftyList list = new ShiftyList();

        int ringOut = 0;
        int listOut = 0;
        boolean sameOrder = true;
        for (int i = 0; i < JOBS; i++) {
            String job = "job" + i;
            ring.push(job);
            list.add(job);
            if (ring.size() == ring.capacity()) {
                ringOut++;
                listOut++;
                String fromRing = ring.poll();
                String fromList = list.removeFirst();
                if (!fromRing.equals(fromList)) {
                    sameOrder = false;
                }
            }
        }

        System.out.printf(Locale.ROOT, "   jobs processed     ring %d, list %d%n", ringOut, listOut);
        System.out.printf(Locale.ROOT, "   same order out     %s%n", sameOrder);
        System.out.printf(Locale.ROOT, "   capacity           ring %d, list %d%n",
                ring.capacity(), list.capacity());
        System.out.printf(Locale.ROOT, "   element moves      ring 0, list %d%n", list.moves());
        System.out.println();

        System.out.println("   (985 of the thousand came out. The last fifteen never filled the window,");
        System.out.println("   so both containers are still holding them - and both held the same ones.)");
        System.out.println();
        System.out.printf(Locale.ROOT, "   The list moved %d elements to hand back %d jobs.%n",
                list.moves(), listOut);
        System.out.println("   The ring moved none - and RingBuffer has no moves() method to print,");
        System.out.println("   because there is nothing in it to count. Taking the oldest item out is");
        System.out.println("   one read and one addition: head = (head + 1) % capacity. Nothing was shifted");
        System.out.println("   because nothing needed to be - the front of a ring is a position, not");
        System.out.println("   a place where data lives.");
        System.out.println();
        System.out.println("   Sizes again, because the ratio is the lesson and not the number:");
        System.out.println("   the list did work proportional to how much it was holding, for every");
        System.out.println("   single removal. Double the window and it doubles. The ring does not");
        System.out.println("   notice. That is the difference between O(n) and O(1) in one measurement.");
        System.out.println();
        System.out.println("   And this is the whole of what ArrayDeque is. Session 11 told you to");
        System.out.println("   replace Stack with it; now you have written one and know why.");
    }

    /**
     * A list that removes its FIRST element and counts what that costs. Each removal copies
     * every element after the gap down one slot - the work a ring buffer never does.
     */
    static final class ShiftyList {

        private String[] data = new String[WINDOW];
        private int size;
        private long moves;

        void add(String value) {
            if (size == data.length) {
                String[] bigger = new String[data.length * 2];
                System.arraycopy(data, 0, bigger, 0, size);
                data = bigger;
            }
            data[size++] = value;
        }

        /** Takes the oldest away, and moves everything else down to close the gap. */
        String removeFirst() {
            if (size == 0) {
                throw new IllegalStateException("nothing to remove");
            }
            String value = data[0];
            System.arraycopy(data, 1, data, 0, size - 1);
            moves += size - 1;
            data[--size] = null;
            return value;
        }

        int capacity() {
            return data.length;
        }

        long moves() {
            return moves;
        }
    }
}
