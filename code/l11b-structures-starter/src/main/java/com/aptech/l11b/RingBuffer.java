package com.aptech.l11b;

/**
 * Lab Task L11B — RingBuffer, a queue with no shifting.   <<< THIS IS THE THIRD FILE YOU COMPLETE >>>
 *
 * R9 — push() adds to the back, pop() takes from the back, poll() takes from the front.
 *      push + pop is a STACK; push + poll is a QUEUE. One class, because the ring does not care
 *      which end you use.
 * R10 — head and tail wrap: after the last slot comes the first one, and nothing is ever shifted
 *      or copied to make that happen.
 * R11 — an empty buffer must refuse rather than return something wrong, and a full one must
 *      refuse rather than overwrite.
 *
 * The array, head, tail and size are given. The constructor and the four one-line accessors are
 * given too, so you can check the wrap from SelfCheck. Everything else is yours.
 *
 * Author: YOUR NAME HERE
 */
public final class RingBuffer {

    private String[] data;
    private int head;      // the slot the next element comes OUT of
    private int tail;      // the slot the next element goes INTO
    private int size;      // what tells "full" apart from "empty"

    public RingBuffer(int capacity) {
        data = new String[Math.max(1, capacity)];
    }

    /** How many elements are waiting. GIVEN. */
    public int size() {
        return size;
    }

    /** How many slots there are. GIVEN. */
    public int capacity() {
        return data.length;
    }

    /** The slot the next element will come out of. GIVEN - SelfCheck uses it to see the wrap. */
    public int head() {
        return head;
    }

    /** The slot the next element will go into. GIVEN. */
    public int tail() {
        return tail;
    }

    /** R11 — True when nothing is stored. */
    public boolean isEmpty() {
        // TODO R11
        return true;
    }

    /** R11 — True when every slot is taken. Note: NOT the same as head == tail. */
    public boolean isFull() {
        // TODO R11
        return false;
    }

    /**
     * R9 + R10 — Stores the value at tail, then moves tail on by one slot, wrapping.
     *
     * The wrap is the whole idea:  tail = (tail + 1) % data.length;
     * R11 first: if the buffer is full, throw IllegalStateException rather than overwrite a
     * value that nobody has read yet.
     */
    public void push(String value) {
        // TODO R9 + R10 + R11
    }

    /**
     * R9 — Removes the NEWEST element and returns it. Use with push() and this is a stack.
     * Move tail back by one slot first (wrapping backwards), then read - and do not forget that
     * tail may be at slot 0 when you start, so subtracting one must wrap to the end.
     * R11: throw IllegalStateException when empty.
     */
    public String pop() {
        // TODO R9 + R11
        return null;
    }

    /**
     * R9 — Removes the OLDEST element and returns it. Use with push() and this is a queue.
     * Read at head, then move head on by one slot (wrapping forwards).
     * R11: throw IllegalStateException when empty.
     */
    public String poll() {
        // TODO R9 + R11
        return null;
    }

    /**
     * The contents from the oldest to the newest, whatever slots they currently occupy, in the
     * form  [a, b, c]  - oldest first, separated by a comma and a space, empty buffer "[]".
     *
     * The element i slots along from head is at  (head + i) % data.length, which is the line
     * that makes the slot order and the queue order two different things.
     */
    @Override
    public String toString() {
        // TODO
        return "[]";
    }
}
