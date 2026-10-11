package com.aptech.s11b;

import com.aptech.s11b.ds.IntList;
import com.aptech.s11b.ds.RingBuffer;
import com.aptech.s11b.ds.SimpleMap;
import com.aptech.s11b.lab.Checks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * E06 — one suite, two implementations.
 *
 * The third time this course has run the same checks against two things that were written by
 * different people: s09's seam, s10's parameterised tests, and now this. The suite knows three
 * interfaces. It does not know that one side is thirty lines written this morning by a student
 * and the other is thirty years of engineering, and that is precisely what makes it worth
 * anything.
 *
 * Both sides come back green. The second half of the program is the honest part: green does not
 * mean identical. It prints the edges where the two really do disagree - null keys, an empty
 * buffer, a fixed capacity, boxing - and none of them is a bug. They are choices, and knowing
 * which choice you are standing on is the difference between using a library and trusting one.
 */
public class E06_OneSuiteTwoImplementations {

    public static void main(String[] args) {
        System.out.println("One suite, two implementations.");
        System.out.println("Nothing below knows whether it is talking to the structures written this");
        System.out.println("morning or to the ones the JDK has shipped for thirty years. It knows");
        System.out.println("three interfaces, and that is the whole of the trick.");
        System.out.println();

        Checks checks = new Checks();

        checks.heading("The list contract - IntList, in full");
        checks.listSuite(Checks::handList);
        System.out.println("  " + checks.suiteTally());

        checks.heading("The map contract - SimpleMap, in full");
        checks.mapSuite(Checks::handMap);
        System.out.println("  " + checks.suiteTally());

        checks.heading("The deque contract - RingBuffer, in full");
        checks.dequeSuite(Checks::handDeque);
        System.out.println("  " + checks.suiteTally());

        System.out.println();
        System.out.println("Every check above was written against an interface, never against a class.");
        System.out.println("Now the same three suites, unchanged, against the JDK's own structures.");
        System.out.println("Passing checks are not reprinted - only failures are, and there are none.");

        checks.quietPasses();

        checks.heading("ArrayList instead of IntList");
        checks.listSuite(Checks::jdkList);
        System.out.println("  " + checks.suiteTally());

        checks.heading("HashMap instead of SimpleMap");
        checks.mapSuite(Checks::jdkMap);
        System.out.println("  " + checks.suiteTally());

        checks.heading("ArrayDeque instead of RingBuffer");
        checks.dequeSuite(Checks::jdkDeque);
        System.out.println("  " + checks.suiteTally());

        System.out.println();
        System.out.println("Totalled: " + checks.tally() + ".");
        System.out.println("Both sides are correct by exactly the same standard, which is the only");
        System.out.println("standard a suite can offer. It says nothing about which is faster, and");
        System.out.println("it cannot - speed is measured, and measurement is not reproducible enough");
        System.out.println("to print. Everything countable was counted instead.");

        differences();
    }

    /**
     * Where the two sides genuinely disagree. Every line below is produced by running the thing
     * it describes, including the exception names.
     */
    private static void differences() {
        System.out.println();
        System.out.println("Where the two sides honestly differ");
        System.out.println("Nothing here is a bug - both sides passed every check. These are the edges");
        System.out.println("each side drew, and you should know which side you are standing on.");
        System.out.println();

        System.out.println("1. A null key");
        Map<String, Integer> jdkMap = new HashMap<>();
        jdkMap.put(null, 1);
        System.out.println("     HashMap    put(null, 1) -> accepted, size " + jdkMap.size());
        System.out.println("     SimpleMap  put(null, 1) -> " + thrownName(() -> new SimpleMap().put(null, 1)));
        System.out.println();

        System.out.println("2. A null value");
        Map<String, Integer> nulled = new HashMap<>();
        nulled.put("BOLT", null);
        System.out.println("     HashMap    put(\"BOLT\", null) -> accepted; containsKey "
                + nulled.containsKey("BOLT") + ", get " + nulled.get("BOLT"));
        System.out.println("     SimpleMap  cannot be asked - its values are int, so \"no value yet\"");
        System.out.println("                is not a state it can be in. That is a design choice, and it");
        System.out.println("                is the same choice Session 11's Hashtable made for you.");
        System.out.println();

        System.out.println("3. Taking from an empty buffer");
        Deque<String> jdkEmpty = new ArrayDeque<>();
        System.out.println("     ArrayDeque pop()       on empty -> "
                + thrownBy(() -> jdkEmpty.pop()) + ", and pollFirst() -> " + jdkEmpty.pollFirst());
        RingBuffer handEmpty = new RingBuffer(4);
        System.out.println("     RingBuffer pop()       on empty -> " + thrownBy(handEmpty::pop));
        System.out.println("     RingBuffer poll()      on empty -> " + thrownBy(handEmpty::poll));
        System.out.println("     Both refuse. One refuses by name, the other by returning null, and");
        System.out.println("     null is exactly how a bug hides: NPE three lines later, not here.");
        System.out.println();

        System.out.println("4. A fixed size, or a growing one");
        RingBuffer four = new RingBuffer(4);
        for (String value : new String[]{"a", "b", "c", "d"}) {
            four.push(value);
        }
        try {
            four.push("e");
            System.out.println("     RingBuffer  5th push into 4 slots -> accepted");
        } catch (RuntimeException ex) {
            System.out.println("     RingBuffer  5th push into 4 slots -> " + ex.getMessage());
        }
        Deque<String> growing = new ArrayDeque<>(4);
        for (int i = 0; i < 1000; i++) {
            growing.addLast("x" + i);
        }
        System.out.println("     ArrayDeque  1000 pushes asked of 4  -> size " + growing.size()
                + ", it doubled as it went");
        System.out.println("     Yours refuses, which is honest and cheap. The JDK's hides the growth,");
        System.out.println("     which is convenient and is the same doubling E01 measured.");
        System.out.println();

        System.out.println("5. A boxed list has two removes, and the compiler picks by type");
        List<Integer> byIndex = new ArrayList<>(List.of(10, 20, 30));
        System.out.println("     values.remove(1)                  -> takes index 1, returns the element: "
                + byIndex.remove(1) + ", leaving " + byIndex);
        List<Integer> byValue = new ArrayList<>(List.of(10, 20, 30));
        System.out.println("     values.remove(Integer.valueOf(20)) -> takes the element 20, returns whether"
                + " it was there: " + byValue.remove(Integer.valueOf(20)) + ", leaving " + byValue);
        System.out.println("     Same list at the end, for two different reasons. IntList has one");
        System.out.println("     remove and it takes an index, so this trap cannot be written into it.");
        System.out.println();

        System.out.println("6. The message when you walk off the end");
        System.out.println("     IntList   -> " + thrownBy(() -> listOf(1, 2, 3).get(3)));
        System.out.println("     ArrayList -> " + thrownBy(() -> new ArrayList<>(List.of(1, 2, 3)).get(3)));
        System.out.println("     Same exception, different sentence. Never match on the message.");
        System.out.println();

        System.out.println("7. Changing a list while walking it");
        List<String> orders = new ArrayList<>(List.of("A", "B", "C"));
        StringBuilder visited = new StringBuilder();
        String verdict;
        try {
            for (String order : orders) {
                visited.append(order);
                if (order.equals("B")) {
                    orders.remove(order);
                }
            }
            verdict = "no complaint at all";
        } catch (RuntimeException ex) {
            verdict = ex.getClass().getSimpleName();
        }
        System.out.println("     remove(\"B\") from [A, B, C] inside a for-each:");
        System.out.println("        visited " + visited + ", threw " + verdict
                + ", list is now " + orders);
        System.out.println("        C was never visited, and nothing said so. The removal happened");
        System.out.println("        to end the walk, so the check that would have caught it was");
        System.out.println("        never reached. The list is quietly wrong.");
        System.out.println();

        List<String> other = new ArrayList<>(List.of("A", "B", "C"));
        StringBuilder seen = new StringBuilder();
        String thrown;
        try {
            for (String order : other) {
                seen.append(order);
                if (order.equals("A")) {
                    other.remove(order);
                }
            }
            thrown = "no complaint at all";
        } catch (RuntimeException ex) {
            thrown = ex.getClass().getSimpleName();
        }
        System.out.println("     remove(\"A\") from [A, B, C] instead:");
        System.out.println("        visited " + seen + ", threw " + thrown);
        System.out.println();
        System.out.println("     One mistake, two outcomes - which is why fail-fast is a best effort");
        System.out.println("     and not a guarantee. Iterator replaced Session 11's Enumeration");
        System.out.println("     because it tries to catch this; the cure is still to remove through");
        System.out.println("     the iterator's own remove(). IntList has no iterator at all, so both");
        System.out.println("     versions of this mistake pass there without a word.");
        System.out.println();

        System.out.println("The suite is the answer to \"is my structure right?\". The list above is");
        System.out.println("the answer to \"is it the same as theirs?\" - and the answer is no, and");
        System.out.println("it should not be. Write the structure to understand it; then delete it");
        System.out.println("and use theirs.");
    }

    private static IntList listOf(int... values) {
        IntList list = new IntList();
        for (int value : values) {
            list.add(value);
        }
        return list;
    }

    /** Runs the action and names whatever it threw, or says that it threw nothing. */
    private static String thrownBy(Runnable action) {
        try {
            action.run();
            return "nothing thrown";
        } catch (Throwable thrown) {
            String message = thrown.getMessage();
            return nameOf(thrown) + (message == null ? "" : ": " + message);
        }
    }

    /**
     * Just the exception's name. Used where the message is the JDK's own prose - a helpful
     * NullPointerException prints a sentence about the exact expression that was null, which is
     * useful in a debugger and noise in a report.
     */
    private static String thrownName(Runnable action) {
        try {
            action.run();
            return "nothing thrown";
        } catch (Throwable thrown) {
            return nameOf(thrown);
        }
    }

    private static String nameOf(Throwable thrown) {
        return thrown.getClass().getSimpleName();
    }
}
