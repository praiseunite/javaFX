package com.aptech.s02;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Example 9 — Type inference: the compiler works out the types so you don't have to repeat them.
 */
public class E09_TypeInference {
    public static void main(String[] args) {
        // 1) The diamond <> (Java 7): the type on the right is copied from the left
        Map<String, List<Integer>> marks = new HashMap<>();   // instead of new HashMap<String, List<Integer>>()
        marks.put("Ada", new ArrayList<>());                  // inferred from put()'s parameter type
        marks.get("Ada").add(88);
        System.out.println("marks = " + marks);

        // 2) Generic METHOD calls: T is worked out from the arguments
        Pair<String, Double> price = Pair.of("Bread", 950.0);  // K = String, V = Double
        System.out.println("price = " + price);

        // 3) Target typing: T is worked out from where the result is going
        List<String> noNames = Collections.emptyList();       // T = String because of the variable's type
        System.out.println("noNames is empty? " + noNames.isEmpty());

        // 4) var (Java 10): the VARIABLE's type is inferred from the right-hand side
        var scores = new ArrayList<Integer>();                // scores is an ArrayList<Integer>
        scores.add(70);
        scores.add(85);
        var best = Collections.max(scores);                   // best is an Integer
        System.out.println("best = " + best);

        // 5) Lambdas: the parameter type s is inferred as String
        Function<String, Integer> length = s -> s.length();
        System.out.println("length(\"JavaFX\") = " + length.apply("JavaFX"));
    }
}
