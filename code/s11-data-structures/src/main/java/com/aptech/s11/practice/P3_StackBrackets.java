package com.aptech.s11.practice;

import java.util.Stack;

/**
 * P3 - Bullet 3, the Stack classes.
 *
 * Bracket matching is the exercise a stack exists for: what you have to remember is what was
 * opened last, and that is exactly what a stack hands back first.
 */
public class P3_StackBrackets {

    public static void main(String[] args) {
        String[] samples = {
            "a(b[c]d)e",
            "a(b[c)d]e",
            "()",
            "())",
            "((",
            "no brackets here",
        };
        for (String sample : samples) {
            System.out.printf("%-18s -> %s%n", sample, check(sample));
        }

        System.out.println();
        System.out.println("The stack also answers WHERE something sits, counted from the top:");
        Stack<Character> open = new Stack<>();
        open.push('(');
        open.push('[');
        open.push('{');
        System.out.println("   the stack          " + open + "   (printed bottom first)");
        System.out.println("   open.search('{')   -> " + open.search('{') + "   (on top)");
        System.out.println("   open.search('[')   -> " + open.search('['));
        System.out.println("   open.search('(')   -> " + open.search('(') + "   (deepest)");
        System.out.println("   open.search(')')   -> " + open.search(')') + "   (not there at all)");
        System.out.println();
        System.out.println("   Counted from 1, from the top - not from 0 at the bottom.");
    }

    private static String check(String text) {
        Stack<Character> open = new Stack<>();
        for (char c : text.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                open.push(c);
            } else if (c == ')' || c == ']' || c == '}') {
                if (open.isEmpty()) {
                    return "closing '" + c + "' with nothing open";
                }
                char top = open.pop();
                if (partnerOf(top) != c) {
                    return "'" + top + "' was closed by '" + c + "'";
                }
            }
        }
        if (!open.isEmpty()) {
            return "never closed: " + open;
        }
        return "balanced";
    }

    private static char partnerOf(char opener) {
        return switch (opener) {
            case '(' -> ')';
            case '[' -> ']';
            default -> '}';
        };
    }
}
