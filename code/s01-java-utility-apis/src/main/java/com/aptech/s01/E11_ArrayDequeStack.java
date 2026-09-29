package com.aptech.s01;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Example 11 — ArrayDeque: one class that works as a Stack (LIFO) AND a Queue (FIFO).
 */
public class E11_ArrayDequeStack {
    public static void main(String[] args) {
        // As a STACK of plates: last in, first out
        Deque<String> history = new ArrayDeque<>();
        history.push("home.html");
        history.push("courses.html");
        history.push("session-1.html");
        System.out.println("History stack: " + history);          // top is printed first
        System.out.println("Back button -> leave " + history.pop());
        System.out.println("Now viewing:   " + history.peek());

        // As a double-ended QUEUE: add or remove at BOTH ends
        Deque<Integer> deque = new ArrayDeque<>();
        deque.offerLast(2);
        deque.offerLast(3);
        deque.offerFirst(1);
        System.out.println("Deque:         " + deque);
        System.out.println("pollFirst -> " + deque.pollFirst() + ", pollLast -> " + deque.pollLast());
    }
}
