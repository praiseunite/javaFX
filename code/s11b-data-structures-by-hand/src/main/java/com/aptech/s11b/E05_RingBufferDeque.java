package com.aptech.s11b;

import com.aptech.s11b.ds.RingBuffer;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * E05 — the ring buffer, where nothing is ever shifted or copied.
 *
 * A list that removes from the front has to move everything down one slot: O(n) work for a
 * single removal. A ring buffer does not move anything. It keeps one array, two indexes and a
 * size, and moves the indexes with a modulo. Removing from the front is one array read and one
 * addition.
 *
 * The trace below follows the diagram on the page slot for slot, so the picture and the output
 * can be read against each other.
 */
public class E05_RingBufferDeque {

    public static void main(String[] args) {
        System.out.println("One array, no shifting, no copying. Two indexes move, and wrap.");
        System.out.println();

        System.out.println("1. Setting the stage");
        RingBuffer ring = new RingBuffer(8);
        ring.push("p");
        ring.push("q");
        System.out.println("   push(p), push(q)   -> " + ring.slotMap());
        System.out.println("                          " + state(ring));
        ring.poll();
        ring.poll();
        System.out.println("   poll(), poll()     -> empty");
        System.out.println("                          " + state(ring));
        System.out.println("   head and tail have both moved to slot 2. The two slots they passed");
        System.out.println("   are free again, and nothing was copied to free them.");
        System.out.println();

        System.out.println("2. Three pushes, and the ring is in use");
        ring.push("a");
        ring.push("b");
        ring.push("c");
        System.out.println("   push(a), push(b), push(c)");
        System.out.println("      " + ring.slotMap());
        System.out.println("      " + state(ring));
        System.out.println("      reading oldest first: " + ring);
        System.out.println();

        System.out.println("3. Four more, and tail runs off the end and comes back");
        ring.push("d");
        ring.push("e");
        ring.push("f");
        ring.push("g");
        System.out.println("   push(d), push(e), push(f), push(g)");
        System.out.println("      " + ring.slotMap());
        System.out.println("      " + state(ring));
        System.out.println("      reading oldest first: " + ring);
        System.out.println();
        System.out.println("   g went into slot 0 - the front of the array - and it is the NEWEST");
        System.out.println("   element. Slot order and queue order have nothing to do with each other.");
        System.out.println("   tail became (7 + 1) % 8 = 0, and that one line is the whole ring.");
        System.out.println();

        System.out.println("4. The same object, used two ways");
        RingBuffer asQueue = new RingBuffer(8);
        asQueue.push("first");
        asQueue.push("second");
        asQueue.push("third");
        System.out.println("   as a queue: push x3, then poll()  -> " + asQueue.poll());
        System.out.println("               (the oldest, which is what a queue gives you)");
        RingBuffer asStack = new RingBuffer(8);
        asStack.push("first");
        asStack.push("second");
        asStack.push("third");
        System.out.println("   as a stack: push x3, then pop()   -> " + asStack.pop());
        System.out.println("               (the newest, which is what a stack gives you)");
        System.out.println();
        System.out.println("   push()/poll() is a queue. push()/pop() is a stack. One class, because");
        System.out.println("   the ring does not care which end you take from.");
        System.out.println();

        System.out.println("5. A thousand turns, and the array never grows");
        RingBuffer turning = new RingBuffer(8);
        for (int i = 0; i < 1000; i++) {
            turning.push("m" + i);
            turning.poll();
        }
        System.out.println("   1000 pushes and 1000 polls later:");
        System.out.println("      capacity " + turning.capacity() + ", size " + turning.size()
                + ", head " + turning.head() + ", tail " + turning.tail());
        System.out.println("   Capacity 8 throughout. Compare that with IntList, which replaces its");
        System.out.println("   array over and over. Neither is better - they are built for different");
        System.out.println("   jobs, and a queue is this one's job.");
        System.out.println();

        System.out.println("6. ArrayDeque is this, with the edges filed off");
        Deque<String> stack = new ArrayDeque<>();
        stack.push("bottom");
        stack.push("top");
        System.out.println("   as a stack: push, push -> " + stack + " ; pop -> " + stack.pop());
        Deque<String> queue = new ArrayDeque<>();
        queue.addLast("first");
        queue.addLast("second");
        queue.addLast("third");
        System.out.println("   as a queue: addLast x3 -> " + queue + " ; pollFirst -> " + queue.pollFirst());
        System.out.println();
        System.out.println("   ArrayDeque is a ring buffer that grows by doubling when it fills, is");
        System.out.println("   not synchronized, and refuses nulls. Session 11 offered it as the");
        System.out.println("   replacement for Stack; now you know what is inside it.");
    }

    private static String state(RingBuffer ring) {
        return "head=" + ring.head() + "  tail=" + ring.tail() + "  size=" + ring.size();
    }
}
