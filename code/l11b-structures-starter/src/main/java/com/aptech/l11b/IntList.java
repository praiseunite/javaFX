package com.aptech.l11b;

/**
 * Lab Task L11B — IntList, a growable array.   <<< THIS IS THE FIRST FILE YOU COMPLETE >>>
 *
 * R1 — add() appends, growing the array first when it is full.
 * R2 — get() returns element i, and throws IndexOutOfBoundsException for anything outside
 *      0 .. size-1. (SelfCheck looks at the exception type, not the message.)
 * R3 — removeAt() takes one element out and closes the gap behind it.
 * R4 — grow() doubles the array and counts the element copies it performs.
 *
 * The array, the size counter and the copies counter are given. The four methods are yours.
 * Run SelfCheck after each one - it tells you which requirement is still red.
 *
 * Author: YOUR NAME HERE
 */
public final class IntList {

    private int[] data;
    private int size;
    private int copies;

    /** A new list with room for four. Small on purpose: you want to watch it grow. */
    public IntList() {
        this(4);
    }

    public IntList(int initialCapacity) {
        data = new int[Math.max(1, initialCapacity)];
    }

    /** How many elements are stored. GIVEN. */
    public int size() {
        return size;
    }

    /** How much room the array has. GIVEN. */
    public int capacity() {
        return data.length;
    }

    /** Total element copies performed by every grow() so far. GIVEN. */
    public int copies() {
        return copies;
    }

    /**
     * R1 — Appends one value.
     * If the array is already full, call grow() FIRST, then store. Do not forget to advance size.
     */
    public void add(int value) {
        // TODO R1
    }

    /**
     * R2 — Returns the element at that index.
     * Throw IndexOutOfBoundsException when the index is below 0 or at/past size.
     * Note that the legal range is the SIZE, not the capacity - the empty slots at the end are
     * not part of the list.
     */
    public int get(int index) {
        // TODO R2
        return 0;
    }

    /**
     * R3 — Removes the element at that index and returns it, keeping the order of the rest.
     * The elements after the gap each move down one slot: System.arraycopy is the one line that
     * does it. Also throw IndexOutOfBoundsException for a bad index, and shrink size.
     */
    public int removeAt(int index) {
        // TODO R3
        return 0;
    }

    /**
     * R4 — Make room: a new array twice as long, everything copied across, the old array dropped.
     * Add to `copies` the number of elements you copied - it is the measurement the lesson is
     * built on, and SelfCheck reads it.
     */
    private void grow() {
        // TODO R4
    }
}
