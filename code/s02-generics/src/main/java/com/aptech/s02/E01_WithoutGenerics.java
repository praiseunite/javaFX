package com.aptech.s02;

import java.util.ArrayList;
import java.util.List;

/**
 * Example 1 — Life BEFORE generics (Java 1.4 and older).
 * A "raw" list accepts anything, so mistakes are only discovered when the program is already running.
 */
public class E01_WithoutGenerics {
    @SuppressWarnings({"rawtypes", "unchecked"})   // hides the compiler's warnings so you can see the crash
    public static void main(String[] args) {
        List names = new ArrayList();      // raw type: no <String>, so it holds plain Objects
        names.add("Ada");
        names.add("Ben");
        names.add(42);                     // oops! a number slipped in - the compiler does not stop us

        for (int i = 0; i < names.size(); i++) {
            try {
                String name = (String) names.get(i);          // we must CAST every time we read
                System.out.println("Hello, " + name.toUpperCase());
            } catch (ClassCastException e) {
                System.out.println("CRASH at index " + i + ": " + e.getMessage());
            }
        }
    }
}
