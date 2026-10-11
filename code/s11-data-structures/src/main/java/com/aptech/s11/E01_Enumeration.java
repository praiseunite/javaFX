package com.aptech.s11;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * E01 — the Enumeration interface: the first way Java let you walk a collection.
 *
 * Manual bullet 1 of 4: "Explain the Enumeration interface".
 *
 * The two interfaces do the same job with different words. The program walks one Vector both
 * ways so the difference is visible, then shows the three places you still meet Enumeration:
 * elements() on a legacy collection, Collections.enumeration(...) on a modern one, and
 * asIterator() when you want to hand an old Enumeration to new code.
 */
public class E01_Enumeration {

    public static void main(String[] args) {
        Vector<String> roll = new Vector<>();
        roll.add("Ada");
        roll.add("Ben");
        roll.add("Chidi");

        System.out.println("The same Vector, walked two ways.");
        System.out.println();

        System.out.println("1. Enumeration - the 1995 way");
        for (Enumeration<String> e = roll.elements(); e.hasMoreElements(); ) {
            System.out.println("   " + e.nextElement());
        }

        System.out.println();
        System.out.println("2. Iterator - the way you already write");
        for (Iterator<String> it = roll.iterator(); it.hasNext(); ) {
            System.out.println("   " + it.next());
        }

        System.out.println();
        System.out.println("3. Collections.enumeration(...) - wrapping a modern List");
        List<String> modern = List.of("Ada", "Ben", "Chidi");
        int seen = 0;
        for (Enumeration<String> e = Collections.enumeration(modern); e.hasMoreElements(); ) {
            e.nextElement();
            seen++;
        }
        System.out.println("   walked " + seen + " elements with no index to get wrong");

        System.out.println();
        System.out.println("4. What Iterator adds that Enumeration never had: remove()");
        Iterator<String> removable = roll.iterator();
        removable.next();
        removable.remove();
        System.out.println("   after it.next(); it.remove();      -> " + roll);

        Iterator<String> wrapped = roll.elements().asIterator();
        wrapped.next();
        try {
            wrapped.remove();
        } catch (UnsupportedOperationException ex) {
            System.out.println("   the same Vector, but via asIterator() -> "
                    + ex.getClass().getName());
        }

        System.out.println();
        System.out.println("Why Iterator replaced it: hasNext()/next() are shorter than");
        System.out.println("hasMoreElements()/nextElement(), it can remove(), it offers");
        System.out.println("forEachRemaining(), and it fails fast when the collection changes");
        System.out.println("underneath you.");
    }
}
