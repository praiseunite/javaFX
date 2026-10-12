package com.aptech.s12.drills;

import java.util.HashMap;
import java.util.Map;

/**
 * DRILL 5 — Book Session 8: Design Patterns &amp; The Architectural Seam.
 *
 * A common beginner defect in enterprise design is allowing persistence details
 * (such as concrete classes or SQLException) to leak across architectural boundaries.
 * The Repository pattern hides storage behind an interface; the Factory isolates instantiation.
 */
public class D5_LeakingTheSeam {

    // === The clean domain interface ===
    public interface StudentStore {
        void save(int id, String name);
        String find(int id);
    }

    // === Implementation 1: In-Memory ===
    public static class MemoryStore implements StudentStore {
        private final Map<Integer, String> data = new HashMap<>();
        @Override public void save(int id, String name) { data.put(id, name); }
        @Override public String find(int id) { return data.get(id); }
    }

    // === Implementation 2: Simulated Database ===
    public static class DatabaseStore implements StudentStore {
        private final Map<Integer, String> db = new HashMap<>();
        @Override public void save(int id, String name) { db.put(id, name); }
        @Override public String find(int id) { return db.get(id); }
    }

    public enum Mode { MEMORY, DATABASE }

    // === Factory with exhaustive switch expression (no default) ===
    public static class StoreFactory {
        public static StudentStore create(Mode mode) {
            return switch (mode) {
                case MEMORY -> new MemoryStore();
                case DATABASE -> new DatabaseStore();
            };
        }
    }

    public static void main(String[] args) {
        System.out.println("Drill 5 - The Architectural Seam: direct coupling vs Factory & Interface");
        System.out.println();

        System.out.println("BROKEN - presentation controller tightly coupled to concrete class:");
        System.out.println("  DatabaseStore store = new DatabaseStore(); // Concrete class leak!");
        System.out.println("  -> Cannot switch to in-memory store for testing without editing every caller.");
        System.out.println();

        System.out.println("FIXED - caller talks only to the interface, obtained through Factory:");
        StudentStore memStore = StoreFactory.create(Mode.MEMORY);
        memStore.save(101, "Ada Lovelace");

        StudentStore dbStore = StoreFactory.create(Mode.DATABASE);
        dbStore.save(102, "Alan Turing");

        System.out.println("  memory store find(101)   : " + memStore.find(101));
        System.out.println("  database store find(102) : " + dbStore.find(102));
        System.out.println("  -> Zero concrete dependencies in the caller; interchangeable at runtime.");
    }
}
