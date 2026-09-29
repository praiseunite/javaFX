package com.aptech.l2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * L2 SELF-CHECK — run this class to test GenericToolkit and Shelf. Aim for all PASS.
 */
public class SelfCheck {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== L2 Self-Check ===\n");

        run("R1 swap() works for Strings and Integers", () -> {
            List<String> s = new ArrayList<>(List.of("a", "b", "c"));
            GenericToolkit.swap(s, 0, 2);
            check(s.equals(List.of("c", "b", "a")), "expected [c, b, a] but got " + s);
            List<Integer> n = new ArrayList<>(List.of(1, 2));
            GenericToolkit.swap(n, 0, 1);
            check(n.equals(List.of(2, 1)), "expected [2, 1] but got " + n);
        });
        run("R2 lastOrDefault() returns the last element or the fallback", () -> {
            check("z".equals(GenericToolkit.lastOrDefault(List.of("x", "y", "z"), "none")), "last of [x, y, z] should be z");
            check("none".equals(GenericToolkit.lastOrDefault(new ArrayList<String>(), "none")), "empty list should give the fallback");
        });
        run("R3 largest() uses compareTo and handles empty lists", () -> {
            check(Integer.valueOf(42).equals(GenericToolkit.largest(List.of(7, 42, 3))), "largest of [7, 42, 3] should be 42");
            check("pear".equals(GenericToolkit.largest(List.of("apple", "pear", "fig"))), "largest word should be pear");
            check(GenericToolkit.largest(new ArrayList<Integer>()) == null, "empty list should return null");
        });
        run("R4 total() adds Integers, Doubles and Longs", () -> {
            check(GenericToolkit.total(List.of(1, 2, 3)) == 6.0, "total of [1, 2, 3] should be 6.0");
            check(GenericToolkit.total(List.of(0.5, 0.25)) == 0.75, "total of [0.5, 0.25] should be 0.75");
            check(GenericToolkit.total(List.of(10L)) == 10.0, "total of [10L] should be 10.0");
            check(GenericToolkit.total(List.of()) == 0.0, "total of [] should be 0.0");
        });
        run("R5 fill() adds a value n times, also into a List of a supertype", () -> {
            List<Integer> ints = new ArrayList<>();
            GenericToolkit.fill(ints, 7, 3);
            check(ints.equals(List.of(7, 7, 7)), "expected [7, 7, 7] but got " + ints);
            List<Number> nums = new ArrayList<>(List.of(1.5));
            GenericToolkit.fill(nums, 2, 2);             // Integers into a List<Number>
            check(nums.toString().equals("[1.5, 2, 2]"), "expected [1.5, 2, 2] but got " + nums);
        });
        run("R6 countMatches() counts equal elements (and nulls)", () -> {
            check(GenericToolkit.countMatches(List.of("a", "b", "a", "a"), "a") == 3, "there are three \"a\"");
            check(GenericToolkit.countMatches(Arrays.asList(1, null, null), null) == 2, "there are two nulls (use Objects.equals)");
            check(GenericToolkit.countMatches(List.of(1, 2), 9) == 0, "9 is not in the list");
        });
        run("R7 Shelf<T> respects its capacity", () -> {
            Shelf<String> shelf = new Shelf<>(2);
            check(shelf.put("book"), "first put should succeed");
            check(shelf.put("lamp"), "second put should succeed");
            check(shelf.isFull(), "shelf of capacity 2 should now be full");
            check(!shelf.put("vase"), "third put should return false");
            check(shelf.size() == 2, "size should stay 2");
        });
        run("R7 Shelf<T> takeLast() and items()", () -> {
            Shelf<Integer> shelf = new Shelf<>(5);
            shelf.put(1);
            shelf.put(2);
            List<Integer> copy = shelf.items();
            copy.clear();                               // changing the copy must not change the shelf
            check(shelf.size() == 2, "items() must return a COPY");
            check(Integer.valueOf(2).equals(shelf.takeLast()), "takeLast() should return 2 (the last added)");
            check(Integer.valueOf(1).equals(shelf.takeLast()), "then 1");
            check(shelf.takeLast() == null, "empty shelf should return null");
        });
        run("R8 invert() swaps keys and values", () -> {
            Map<String, Integer> codes = new LinkedHashMap<>();
            codes.put("NG", 234);
            codes.put("GH", 233);
            Map<Integer, String> inverted = GenericToolkit.invert(codes);
            check(inverted != null && inverted.toString().equals("{234=NG, 233=GH}"), "expected {234=NG, 233=GH} but got " + inverted);
        });

        System.out.println("\n" + passed + " passed, " + failed + " failed.");
        System.out.println(failed == 0 ? "All requirements met - great work!" : "Fix the FAILs above, then run SelfCheck again.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void run(String name, Runnable test) {
        try {
            test.run();
            passed++;
            System.out.println("PASS  " + name);
        } catch (AssertionError e) {
            failed++;
            System.out.println("FAIL  " + name + "\n      -> " + e.getMessage());
        } catch (RuntimeException e) {
            failed++;
            System.out.println("FAIL  " + name + "\n      -> crashed with " + e);
        }
    }
}
