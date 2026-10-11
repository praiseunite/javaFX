package com.aptech.s11b.ds;

/**
 * A ring buffer, built by hand: one array, no shifting, no copying, ever.
 *
 * The array is treated as a circle. {@code head} is the slot the next element comes out of,
 * {@code tail} is the slot the next element goes into, and both move with
 *
 *     index = (index + 1) % data.length;
 *
 * so after the last slot they wrap to the first. Nothing is shifted when something is removed,
 * which is the whole reason this beats a list for a queue.
 *
 * A size counter is what tells "full" apart from "empty". With head and tail alone they would be
 * equal in both states - the classic ring-buffer bug.
 *
 * With push()/pop() it is a stack; with push()/poll() it is a queue. That is ArrayDeque: one
 * class that is both, because the ring does not care which end you use.
 */
public class RingBuffer {

    private String[] data;
    private int head;
    private int tail;
    private int size;

    public RingBuffer(int capacity) {
        data = new String[Math.max(1, capacity)];
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return data.length;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == data.length;
    }

    /** The slot the next element will come out of. Printed by E05 so the wrap is visible. */
    public int head() {
        return head;
    }

    /** The slot the next element will go into. */
    public int tail() {
        return tail;
    }

    /** Adds to the back. The one line that makes it a ring is the tail arithmetic. */
    public void push(String value) {
        if (isFull()) {
            throw new IllegalStateException("ring buffer is full: " + capacity() + " slots");
        }
        data[tail] = value;
        tail = (tail + 1) % data.length;
        size++;
    }

    /** Removes from the back - the newest. Use with push() and this is a stack. */
    public String pop() {
        checkNotEmpty();
        tail = (tail - 1 + data.length) % data.length;
        String value = data[tail];
        data[tail] = null;
        size--;
        return value;
    }

    /** Removes from the front - the oldest. Use with push() and this is a queue. */
    public String poll() {
        checkNotEmpty();
        String value = data[head];
        data[head] = null;
        head = (head + 1) % data.length;
        size--;
        return value;
    }

    public String peekFront() {
        checkNotEmpty();
        return data[head];
    }

    public String peekBack() {
        checkNotEmpty();
        return data[(tail - 1 + data.length) % data.length];
    }

    /** The contents from the oldest to the newest, whatever slots they currently occupy. */
    public String[] inOrder() {
        String[] out = new String[size];
        for (int i = 0; i < size; i++) {
            out[i] = data[(head + i) % data.length];
        }
        return out;
    }

    /** Which slot each live element is sitting in, so the wrap is visible. */
    public String slotMap() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int slot = (head + i) % data.length;
            if (text.length() > 0) {
                text.append(", ");
            }
            text.append("slot ").append(slot).append("=").append(data[slot]);
        }
        return text.toString();
    }

    /** Prints oldest-first, which is not the slot order - that is the point. */
    @Override
    public String toString() {
        return "[" + String.join(", ", inOrder()) + "]";
    }

    private void checkNotEmpty() {
        if (size == 0) {
            throw new IllegalStateException("ring buffer is empty");
        }
    }
}
