package com.aptech.s06.tyi;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * TRY IT YOURSELF 1 — Sessions 1: Utility APIs and the Collection framework.
 *
 * Three everyday jobs, and the one collection shape that actually fits each of them.
 * The skill being tested is not "can you call add()" — it is choosing the right shape
 * before you write a line of it.
 *
 *   1. a sign-in register   -> each student counts once        -> Set
 *   2. a photocopier queue  -> first come, first served        -> Queue
 *   3. grades by student id -> find a value by its key         -> Map
 */
public class T1_Collections {

    public static void main(String[] args) {
        System.out.println("Try It Yourself 1 - choosing the collection");

        // 1. Every student who signed in today. A second sign-in is not a second student.
        //    LinkedHashSet: no duplicates, and it keeps the order they arrived in.
        Set<String> signedIn = new LinkedHashSet<>();
        signedIn.add("S-1001");
        signedIn.add("S-1002");
        signedIn.add("S-1001");                 // signed in again after lunch
        System.out.println("1. sign-in register  : " + signedIn
                + "   (" + signedIn.size() + " distinct students)");

        // 2. The photocopier. Whoever put their work down first goes first.
        Queue<String> copier = new ArrayDeque<>();
        copier.add("Ada");
        copier.add("Ben");
        copier.add("Chioma");

        StringBuilder order = new StringBuilder();
        while (!copier.isEmpty()) {
            order.append(copier.poll());
            if (!copier.isEmpty()) {
                order.append(" -> ");
            }
        }
        System.out.println("2. photocopier order : " + order);

        // 3. Grades, looked up by student id. A HashMap is a lookup table, not a list.
        Map<String, String> grades = new HashMap<>();
        grades.put("S-1001", "A");
        grades.put("S-1002", "B+");

        System.out.println("3. grade for S-1002  : " + grades.get("S-1002"));
        System.out.println("   grade for S-9999  : " + grades.getOrDefault("S-9999", "not on the register")
                + "   (get() would hand you null - say what you want instead)");

        System.out.println();
        System.out.println("The rule: name the job in English first. Unique -> Set. Waiting -> Queue.");
        System.out.println("Look up by something -> Map. Everything else -> List.");
    }
}
