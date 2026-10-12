package com.aptech.s12.tyi;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * TRY IT YOURSELF 5 — Book Session 10: Java Data Structures.
 *
 * Task: Use java.util.BitSet to implement a space-efficient user permission system
 * with bitwise operations, and compare legacy Vector &amp; Enumeration traversal with modern List.
 */
public class T5_LegacyDataStructures {

    // Permission bit positions
    private static final int PERM_READ    = 0;
    private static final int PERM_WRITE   = 1;
    private static final int PERM_DELETE  = 2;
    private static final int PERM_ADMIN   = 3;

    public static void main(String[] args) {
        System.out.println("Try It Yourself 5 - Java Data Structures: BitSet & Legacy Vector");

        // 1. BitSet permissions
        BitSet userPerms = new BitSet();
        userPerms.set(PERM_READ);
        userPerms.set(PERM_WRITE);

        BitSet adminPerms = new BitSet();
        adminPerms.set(PERM_READ);
        adminPerms.set(PERM_WRITE);
        adminPerms.set(PERM_DELETE);
        adminPerms.set(PERM_ADMIN);

        System.out.println();
        System.out.println("1. BitSet permission masks:");
        System.out.println("  User permissions (bits set)  : " + userPerms + " (cardinality: " + userPerms.cardinality() + ")");
        System.out.println("  Admin permissions (bits set) : " + adminPerms + " (cardinality: " + adminPerms.cardinality() + ")");
        System.out.println("  User has READ?   " + userPerms.get(PERM_READ));
        System.out.println("  User has DELETE? " + userPerms.get(PERM_DELETE));

        // Bitwise AND intersection
        BitSet intersection = (BitSet) userPerms.clone();
        intersection.and(adminPerms);
        System.out.println("  Intersection (AND)           : " + intersection);

        // Bitwise XOR difference
        BitSet diff = (BitSet) adminPerms.clone();
        diff.xor(userPerms);
        System.out.println("  Admin-only extra perms (XOR) : " + diff + " (bits: DELETE and ADMIN)");

        // 2. Legacy Vector & Enumeration vs modern List
        System.out.println();
        System.out.println("2. Legacy Vector with Enumeration (JDK 1.0, 1995):");
        Vector<String> legacyLog = new Vector<>();
        legacyLog.add("Audit: User Alice logged in");
        legacyLog.add("Audit: File exported");
        legacyLog.add("Audit: User Alice logged out");

        Enumeration<String> en = legacyLog.elements();
        while (en.hasMoreElements()) {
            System.out.println("  [Enumeration] " + en.nextElement());
        }

        System.out.println();
        System.out.println("3. Modern List equivalent (JDK 21+):");
        List<String> modernLog = new ArrayList<>(legacyLog);
        for (String entry : modernLog) {
            System.out.println("  [EnhancedFor] " + entry);
        }

        System.out.println();
        System.out.println("Rule: Use BitSet for high-density bit flags (1 bit per boolean).");
        System.out.println("Replace legacy Vector with ArrayList, and Enumeration with Iterator.");
    }
}
