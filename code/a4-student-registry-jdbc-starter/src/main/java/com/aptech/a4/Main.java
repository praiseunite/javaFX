package com.aptech.a4;

import java.util.List;

/**
 * A4 MAIN — a short demonstration of the finished registry.
 *
 * Once SelfCheck reports all PASS, run this to watch the registry being used end to end.
 * It writes to ./data/a4-registry.mv.db, so the data survives the program — run it twice
 * and you will see the first run's students still there.
 *
 * If it throws UnsupportedOperationException, that method is still a TODO.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        // A file-backed database: the data is still here next time you run this.
        String url = "jdbc:h2:./data/a4-registry";
        StudentRegistry registry = new StudentRegistry(url);

        System.out.println("A4 - the Student Registry, on a database");

        registry.createSchema();
        System.out.println("\n  schema ready; " + registry.count() + " students before we start");

        if (registry.searchByName("Ada").isEmpty()) {
            int ada   = registry.insert("Ada Lovelace",     "ada@example.com",      1, 88.50);
            int grace = registry.insert("Grace Hopper",     "grace@example.com",    1, 91.00);
            int alan  = registry.insert("Alan Turing",      "alan@example.com",     1, 76.25);
            System.out.println("  inserted Ada, Grace and Alan (ids " + ada + ", " + grace + ", " + alan + ")");
        } else {
            System.out.println("  (the students from the last run are still here)");
        }

        System.out.println("\n  the whole registry:");
        for (StudentRegistry.Student s : registry.searchByName("")) System.out.println("    " + s);

        System.out.println("\n  search for 'a' (a partial name):");
        List<StudentRegistry.Student> found = registry.searchByName("a");
        System.out.println("    " + found.size() + " match(es)");

        System.out.println("\n  search for \"' OR '1'='1\" (an injection attempt):");
        System.out.println("    " + registry.searchByName("' OR '1'='1").size()
                + " match(es) - the payload is data, not SQL");

        System.out.println("\n  the students table, as the database describes it:");
        System.out.println("    " + registry.columnNames());

        System.out.println("\n  total students now: " + registry.count());
        System.out.println("\nClose this program and run it again - the data will still be there.");
        System.out.println("That is the difference between a List and a table.");
    }
}
