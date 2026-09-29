package com.aptech.s01;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Example 10 — A Queue is a line at the bank: First In, First Out (FIFO).
 */
public class E10_QueueDemo {
    public static void main(String[] args) {
        Queue<String> bankLine = new LinkedList<>();

        bankLine.offer("Ada");             // joins the back of the line
        bankLine.offer("Ben");
        bankLine.offer("Chi");
        System.out.println("Line:        " + bankLine);

        System.out.println("Next up:     " + bankLine.peek());   // look, don't remove
        System.out.println("Served:      " + bankLine.poll());   // remove from the front
        System.out.println("Served:      " + bankLine.poll());
        System.out.println("Line now:    " + bankLine);

        bankLine.poll();                   // Chi served
        System.out.println("Empty poll:  " + bankLine.poll());   // empty -> null, no crash
        System.out.println("Empty peek:  " + bankLine.peek());
    }
}
