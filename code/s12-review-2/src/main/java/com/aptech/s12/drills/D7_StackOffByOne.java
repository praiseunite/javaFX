package com.aptech.s12.drills;

import java.util.Stack;

/**
 * DRILL 7 — Book Session 10: Legacy Java Data Structures &amp; Stack.search().
 *
 * Developers familiar with 0-indexed lists expect Stack.search(x) to return 0 for the top item.
 * But java.util.Stack.search() returns 1-based distance from the top of the stack!
 * The top element returns 1; the next returns 2; missing returns -1.
 */
public class D7_StackOffByOne {

    public static void main(String[] args) {
        Stack<String> history = new Stack<>();
        history.push("Home");
        history.push("Courses");
        history.push("Profile"); // Top of the stack

        System.out.println("Drill 7 - Legacy Stack.search() off-by-one index trap");
        System.out.println("Stack contents from bottom to top: " + history);
        System.out.println("Top element is: " + history.peek());
        System.out.println();

        System.out.println("BROKEN - assuming Stack.search() works like 0-indexed indexOf():");
        int assumedZeroIndex = history.search("Profile");
        System.out.println("  history.search(\"Profile\") = " + assumedZeroIndex);
        if (assumedZeroIndex == 0) {
            System.out.println("  -> Matched top at index 0");
        } else {
            System.out.println("  -> Mismatch! Developer tested (pos == 0) and thought Profile was NOT at the top.");
        }
        System.out.println();

        System.out.println("FIXED - respecting the 1-based distance from the top contract:");
        int topDistance = history.search("Profile");
        int nextDistance = history.search("Courses");
        int bottomDistance = history.search("Home");
        int missingDistance = history.search("NonExistent");

        System.out.println("  distance to 'Profile' (top)    : " + topDistance + " (1 = top of stack)");
        System.out.println("  distance to 'Courses' (middle) : " + nextDistance + " (2 = one below top)");
        System.out.println("  distance to 'Home' (bottom)    : " + bottomDistance + " (3 = two below top)");
        System.out.println("  distance to 'NonExistent'      : " + missingDistance + " (-1 = not found)");
        System.out.println("  -> Rule: 1-based distance, not 0-based array index.");
    }
}
