package com.aptech.s11;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

/**
 * E03 — the Stack classes.
 *
 * Manual bullet 3 of 4: "Describe the Stack classes". The plural is doing real work: the class
 * the manual calls Stack is built on ANOTHER legacy class, Vector, and that inheritance is the
 * reason Stack is no longer recommended. So this example is about two classes: Stack, and the
 * Vector it is-a.
 */
public class E03_Stack {

    public static void main(String[] args) {
        lastInFirstOut();
        oneBasedFromTheTop();
        itIsAVector();
        theGapBetweenTwoCalls();
        theReplacement();
    }

    private static void lastInFirstOut() {
        System.out.println("1. A stack is last in, first out");
        Stack<String> plates = new Stack<>();
        plates.push("bottom");
        plates.push("middle");
        plates.push("top");
        System.out.println("   pushed bottom, middle, top -> " + plates);
        System.out.println("   peek()  -> " + plates.peek() + "   (look without taking)");
        System.out.println("   pop()   -> " + plates.pop());
        System.out.println("   after pop() -> " + plates);
        System.out.println();
    }

    private static void oneBasedFromTheTop() {
        System.out.println("2. search() counts from 1 at the TOP, not 0 at the bottom");
        Stack<String> plates = new Stack<>();
        plates.push("bottom");
        plates.push("middle");
        plates.push("top");
        System.out.println("   plates.search(\"top\")     -> " + plates.search("top") + "   (the top is 1)");
        System.out.println("   plates.search(\"middle\")  -> " + plates.search("middle"));
        System.out.println("   plates.search(\"bottom\")  -> " + plates.search("bottom"));
        System.out.println("   plates.search(\"missing\") -> " + plates.search("missing") + "   (-1 when it is not there)");
        System.out.println();
        System.out.println("   So a Stack's position is 1-based AND measured from the top, while an");
        System.out.println("   ArrayList's index is 0-based from the bottom. Code that mixes the two");
        System.out.println("   is off by one, and the lab has exactly that bug.");
        System.out.println();
    }

    private static void itIsAVector() {
        System.out.println("3. Stack extends Vector - so a Stack is a Vector, with everything that implies");
        Stack<String> plates = new Stack<>();
        plates.push("bottom");
        plates.push("top");
        System.out.println("   plates instanceof Vector -> " + (plates instanceof Vector));
        System.out.println("   plates instanceof List   -> " + (plates instanceof List));
        plates.add(0, "sneaked in at the bottom");
        System.out.println("   after plates.add(0, ...) -> " + plates);
        System.out.println("   plates.get(1)            -> " + plates.get(1));
        System.out.println();
        System.out.println("   A stack has two operations that matter. This one has inherited dozens,");
        System.out.println("   so it can be reached into at any index, in any order. The design mistake");
        System.out.println("   is is-a where has-a was meant: a Stack is not a Vector with extras.");
        System.out.println();
    }

    private static void theGapBetweenTwoCalls() {
        System.out.println("4. Every Vector method is synchronized - and that is still not enough");
        Vector<String> shelf = new Vector<>();
        shelf.add("Ada");
        System.out.println("   Start: " + shelf);
        System.out.println();
        System.out.println("   Two callers each run the SAME two steps: isEmpty(), then remove(0).");
        System.out.println("   Each step is locked. Written in an order a scheduler may produce:");
        System.out.println();
        System.out.println("   A: isEmpty()  -> " + shelf.isEmpty());
        System.out.println("   B: isEmpty()  -> " + shelf.isEmpty());
        System.out.println("   A: remove(0)  -> " + shelf.remove(0));
        try {
            shelf.remove(0);
            System.out.println("   B: remove(0)  -> (no error)");
        } catch (RuntimeException ex) {
            System.out.println("   B: remove(0)  -> " + ex);
        }
        System.out.println();
        System.out.println("   Neither call was interrupted. Neither method was unsafe on its own. The");
        System.out.println("   bug lives in the GAP BETWEEN them, and synchronized inside Vector cannot");
        System.out.println("   close a gap it cannot see. Locking each step is not locking the sequence.");
        System.out.println();
    }

    private static void theReplacement() {
        System.out.println("5. The replacement: ArrayDeque, used through the Deque interface");
        Deque<String> plates = new ArrayDeque<>();
        plates.push("bottom");
        plates.push("top");
        System.out.println("   push(), push() -> " + plates + "   (printed from the head)");
        System.out.println("   peek()         -> " + plates.peek());
        System.out.println("   pop()          -> " + plates.pop());
        System.out.println("   after pop()    -> " + plates);
        System.out.println();
        System.out.println("   ArrayDeque is a stack and a queue in one class, it is not synchronized, and");
        System.out.println("   it cannot be indexed - so the mistake in Part 3 is impossible to make.");
        System.out.println("   Prefer it unless you genuinely need Vector's per-method locking.");
    }
}
