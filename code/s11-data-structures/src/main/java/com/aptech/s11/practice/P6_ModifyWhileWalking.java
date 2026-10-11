package com.aptech.s11.practice;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * P6 - challenge. Three ways to walk a list and take things out of it.
 *
 * Two of the three are wrong, and only one of the wrong ones tells you. Work out which is
 * which before you run it.
 */
public class P6_ModifyWhileWalking {

    public static void main(String[] args) {
        System.out.println("Task: sum the odd numbers, and remove the even ones on the way.");
        System.out.println("The correct sum is 1 + 3 + 5 = 9. Watch what each loop really does.");
        System.out.println();

        System.out.println("1. Enumeration, removing during the walk");
        Vector<Integer> legacy = new Vector<>(List.of(1, 2, 3, 4, 5, 6));
        int seen = 0;
        Enumeration<Integer> walk = legacy.elements();
        while (walk.hasMoreElements()) {
            Integer n = walk.nextElement();
            if (n % 2 == 0) {
                legacy.remove(n);
            } else {
                seen += n;
            }
        }
        System.out.println("   odd numbers summed -> " + seen);
        System.out.println("   list afterwards    -> " + legacy);
        System.out.println("   No exception. Also no 3 and no 5: removing shifted the entries left,");
        System.out.println("   so the walk stepped straight over them. An Enumeration is not fail-fast.");
        System.out.println();

        System.out.println("2. Iterator, removing through the iterator");
        List<Integer> modern = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
        int total = 0;
        for (Iterator<Integer> it = modern.iterator(); it.hasNext(); ) {
            Integer n = it.next();
            if (n % 2 == 0) {
                it.remove();
            } else {
                total += n;
            }
        }
        System.out.println("   odd numbers summed -> " + total);
        System.out.println("   list afterwards    -> " + modern);
        System.out.println();

        System.out.println("3. for-each, removing from the list itself");
        try {
            List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
            for (Integer n : list) {
                if (n % 2 == 0) {
                    list.remove(n);
                }
            }
            System.out.println("   no exception - unexpected, see the note below");
        } catch (ConcurrentModificationException ex) {
            System.out.println("   for-each + list.remove(n) -> " + ex);
        }
        System.out.println("   Same mistake as loop 1, caught this time. ArrayList's iterator counts");
        System.out.println("   its own changes and refuses to carry on.");
        System.out.println();
        System.out.println("   Three loops, one intent, three different outcomes. The class you chose");
        System.out.println("   decided whether you got the right answer, a crash, or a silent lie.");
    }
}
