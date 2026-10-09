package com.aptech.s09.repository;

import com.aptech.s09.config.AppConfig;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Builds the right {@link StudentRepository} for a name, so no other class has to know which
 * implementations exist.
 *
 * <p>This is the Factory pattern, and here is the test for whether you need one: <em>how many
 * places name a concrete implementation?</em> Before this class, it was every class that
 * enrolled a student. After it, it is this file.
 *
 * <p>Two smaller habits are worth copying from it:
 * <ul>
 *   <li>The {@code switch} is over the {@link Store} enum with no {@code default} branch, so
 *       adding a store is a <strong>compile error</strong> here until it is handled — not a
 *       runtime surprise three weeks later.</li>
 *   <li>The error message is built from {@code Store.values()}, so it can never list a store
 *       that no longer exists, or forget one that does.</li>
 * </ul>
 */
public final class RepositoryFactory {

    /** The stores this project can build. Add one, and the switch below stops compiling. */
    public enum Store {
        MEMORY,
        H2
    }

    private RepositoryFactory() {
    }

    /** The whole factory: one switch, one new, and the caller never sees either. */
    public static StudentRepository create(Store store) {
        return switch (store) {
            case MEMORY -> new InMemoryStudentRepository();
            case H2 -> new H2StudentRepository();
        };
    }

    /** The same, from a name such as {@code "h2"} — the form a config file would use. */
    public static StudentRepository create(String name) {
        return create(parse(name));
    }

    /** Whatever {@code AppConfig} says. This is the call the rest of the program makes. */
    public static StudentRepository createDefault() {
        return create(AppConfig.get().studentStore());
    }

    /** Turns {@code "h2"} into {@link Store#H2}, or explains what was expected. */
    public static Store parse(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("store name is blank - " + help());
        }
        try {
            return Store.valueOf(name.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("unknown store '" + name + "' - " + help());
        }
    }

    /** The list of valid names, built from the enum so it cannot go stale. */
    public static String help() {
        return "known stores: " + Arrays.stream(Store.values())
                .map(s -> s.name().toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(", "));
    }

    /**
     * A store that already holds the registry {@code db/seed.sql} describes.
     *
     * <p>The H2 store needs nothing doing — the students are in the file. The in-memory store
     * starts empty on purpose, because an empty store is what makes it a good test store, so
     * the factory fills it from the database on the way out. A demo that wants to look at the
     * whole registry asks for this one; a test asks for {@link #create}.
     */
    public static StudentRepository createSeeded(Store store) {
        return switch (store) {
            case MEMORY -> new InMemoryStudentRepository(new H2StudentRepository().findAll());
            case H2 -> new H2StudentRepository();
        };
    }
}
