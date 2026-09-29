package com.aptech.s01.lab;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * GUIDED LAB — The Library Desk.
 * One small program that uses every collection from Session 1:
 *   List  -> the book catalogue (ordered, may contain two copies of a title)
 *   Set   -> library members (no one can join twice)
 *   Map   -> loans: which member has borrowed which book
 *   Queue -> the reservation line for a popular book
 *   Arrays-> sorting the pages-read numbers for a reading challenge
 */
public class LibraryDesk {
    public static void main(String[] args) {
        // STEP 2 — the catalogue is a List
        List<String> catalogue = new ArrayList<>();
        catalogue.add("Things Fall Apart");
        catalogue.add("Half of a Yellow Sun");
        catalogue.add("Purple Hibiscus");
        catalogue.add("Things Fall Apart");            // a second copy - Lists allow duplicates
        Collections.sort(catalogue);
        System.out.println("Catalogue (" + catalogue.size() + " books): " + catalogue);

        // STEP 3 — members are a Set
        Set<String> members = new LinkedHashSet<>();
        members.add("Ada");
        members.add("Ben");
        members.add("Chi");
        boolean joinedAgain = members.add("Ada");      // already a member
        System.out.println("Members: " + members + "  (Ada joined twice? " + joinedAgain + ")");

        // STEP 4 — loans are a Map: book title -> member name
        Map<String, String> loans = new HashMap<>();
        loans.put("Purple Hibiscus", "Ben");
        loans.put("Half of a Yellow Sun", "Ada");
        System.out.println("Who has Purple Hibiscus? " + loans.get("Purple Hibiscus"));
        System.out.println("Is Things Fall Apart on loan? " + loans.containsKey("Things Fall Apart"));
        loans.remove("Purple Hibiscus");                // Ben returns the book
        System.out.println("Loans after Ben returns his book: " + loans);

        // STEP 5 — reservations are a Queue (first to reserve = first to borrow)
        Queue<String> reservations = new ArrayDeque<>();
        reservations.offer("Chi");
        reservations.offer("Ben");
        reservations.offer("Ada");
        System.out.println("Reservation line: " + reservations);
        String nextBorrower = reservations.poll();
        loans.put("Half of a Yellow Sun", nextBorrower);
        System.out.println(nextBorrower + " borrows the book. Still waiting: " + reservations);

        // STEP 6 — the reading challenge uses the Arrays helper class
        int[] pagesRead = {120, 45, 300, 210, 90};
        Arrays.sort(pagesRead);
        System.out.println("Pages read (sorted): " + Arrays.toString(pagesRead));
        System.out.println("Winner read " + pagesRead[pagesRead.length - 1] + " pages");

        // STEP 7 — a quick report
        System.out.println("--- Desk report ---");
        for (Map.Entry<String, String> loan : loans.entrySet()) {
            System.out.println(loan.getValue() + " has \"" + loan.getKey() + "\"");
        }
    }
}
