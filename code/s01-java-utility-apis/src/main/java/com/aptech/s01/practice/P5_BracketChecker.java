package com.aptech.s01.practice;

import java.util.ArrayDeque;
import java.util.Deque;

/** Practice 5 (challenge) — Are the brackets balanced? Uses ArrayDeque as a stack. */
public class P5_BracketChecker {

    static boolean isBalanced(String text) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : text.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);                                  // remember the opener
            } else if (c == ')' || c == ']' || c == '}') {
                if (stack.isEmpty()) {
                    return false;                               // a closer with no opener
                }
                char open = stack.pop();                        // the most recent opener
                if ((c == ')' && open != '(') || (c == ']' && open != '[') || (c == '}' && open != '{')) {
                    return false;                               // wrong kind of bracket
                }
            }
        }
        return stack.isEmpty();                                 // leftovers = unclosed openers
    }

    public static void main(String[] args) {
        String[] tests = {"(a + b) * [c]", "{[()]}", "(]", "((x)", "list.get(0))"};
        for (String t : tests) {
            System.out.println(t + "  ->  " + (isBalanced(t) ? "balanced" : "NOT balanced"));
        }
    }
}
