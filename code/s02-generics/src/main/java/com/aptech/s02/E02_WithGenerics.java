package com.aptech.s02;

import java.util.ArrayList;
import java.util.List;

/**
 * Example 2 — The same program WITH generics.
 * The compiler now checks every add(), and reading needs no cast.
 */
public class E02_WithGenerics {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>();   // "a list of Strings" - the compiler remembers this
        names.add("Ada");
        names.add("Ben");
        // names.add(42);                        // COMPILE ERROR: int cannot be converted to String

        for (String name : names) {               // no cast needed: get() already returns String
            System.out.println("Hello, " + name.toUpperCase());
        }
    }
}
