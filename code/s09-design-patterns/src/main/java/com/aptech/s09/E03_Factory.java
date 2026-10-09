package com.aptech.s09;

import com.aptech.s09.config.AppConfig;
import com.aptech.s09.repository.RepositoryFactory;
import com.aptech.s09.repository.StudentRepository;

/**
 * Example 3 — asking for a store by name, and getting the right class back.
 *
 * <p>The factory is small enough to read in one sitting. What is worth watching is the third
 * section: asking for a store that does not exist produces a message that lists the ones that
 * do, and that list is built from the enum rather than typed out — so it cannot go stale.
 */
public final class E03_Factory {

    public static void main(String[] args) throws Exception {
        Db.resetRegistry();

        System.out.println(Db.rule("Ask for a store by name"));
        for (String name : new String[] { "memory", "h2", "MEMORY" }) {
            StudentRepository repo = RepositoryFactory.create(name);
            System.out.println("  create(\"" + name + "\")"
                    + " ".repeat(Math.max(0, 9 - name.length()))
                    + " -> " + repo.getClass().getName());
        }

        System.out.println(Db.rule("The caller never names an implementation"));
        StudentRepository repo = RepositoryFactory.create("h2");
        System.out.println("  the variable's type is StudentRepository, so the only methods");
        System.out.println("  available are the eight on the interface. There is no way to call");
        System.out.println("  anything H2-specific from here, even by accident.");

        System.out.println(Db.rule("Ask for a store that does not exist"));
        try {
            RepositoryFactory.create("mysql");
        } catch (IllegalArgumentException e) {
            System.out.println("  IllegalArgumentException: " + e.getMessage());
        }

        System.out.println(Db.rule("The list of stores is built from the enum"));
        System.out.println("  " + RepositoryFactory.help());
        System.out.println("  Store.values() holds " + RepositoryFactory.Store.values().length + " entries.");

        System.out.println(Db.rule("Ask for whatever the configuration says"));
        StudentRepository configured = RepositoryFactory.createDefault();
        System.out.println("  AppConfig says         : " + AppConfig.get());
        System.out.println("  createDefault() gave   : " + configured.getClass().getSimpleName()
                + " (" + configured.storeName() + ")");

        System.out.println(Db.rule("Who in this project names a concrete store?"));
        System.out.println("  In a real project: RepositoryFactory, and nothing else.");
        System.out.println("  In these examples: Example 2 names H2 and the in-memory store on purpose,");
        System.out.println("  to show what the factory is hiding. Everywhere else asks the factory.");
    }
}
