package com.aptech.a5.repository;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * R3 — the one place that decides which store you get.   <<< WRITE create() >>>
 *
 * <p>Nothing else in the program may name {@code InMemoryStudentRepository} or
 * {@code H2StudentRepository}. If a second class has to know which one is in use, the choice has
 * leaked, and the point of R1 and R2 is lost.
 *
 * <p>Write {@link #create(Store)} as a <b>switch expression with no {@code default}</b>. That is
 * not an accident of style: with every constant listed, adding a fourth constant to
 * {@link Store} stops the program compiling until somebody decides what it means. A
 * {@code default} would silently return the wrong store instead — and there is no test for a bug
 * that never throws.
 *
 * <p>{@link #create(String)} and {@link #help()} below are already written, so the failure message
 * for a bad name is the same in every student's program.
 */
public final class RepositoryFactory {

    private RepositoryFactory() {
    }

    /** The stores this build knows how to make. Adding one here should break the compile. */
    public enum Store {
        MEMORY,
        H2
    }

    /**
     * TODO R3 — return a new store for this choice.
     *
     * <p>MEMORY is {@code new InMemoryStudentRepository()}. H2 is
     * {@code new H2StudentRepository()} — the default URL, i.e. the real registry file.
     */
    public static StudentRepository create(Store store) {
        throw new UnsupportedOperationException("R3: RepositoryFactory.create(Store) not implemented");
    }

    /**
     * TODO R3 — the store named by the configuration, whatever that turns out to be.
     *
     * <p>{@code AppConfig.get().studentStore()} gives you a {@link Store}; this method is the one
     * line that connects the configuration to the factory. It is also the only method the rest of
     * the program needs to call.
     */
    public static StudentRepository createDefault() {
        throw new UnsupportedOperationException("R3: RepositoryFactory.createDefault() not implemented");
    }

    /**
     * The store named by a String — the value of {@code -Dregistry.store=...}. Already written.
     *
     * <p>Note the two error cases. An unknown name is a typo, so the message lists the valid ones
     * rather than saying "invalid". A name that differs only in case is NOT a typo worth failing
     * over — {@code MEMORY} and {@code memory} mean the same thing to a person, so they are
     * normalised before the lookup.
     */
    public static StudentRepository create(String name) {
        return create(parse(name));
    }

    /** Turns a configuration string into a {@link Store}, or explains what was expected. */
    public static Store parse(String name) {
        String wanted = name == null ? "" : name.trim().toUpperCase(java.util.Locale.ROOT);
        for (Store store : Store.values()) {
            if (store.name().equals(wanted)) {
                return store;
            }
        }
        throw new IllegalArgumentException("unknown store '" + name + "' - " + help());
    }

    /** The valid names, taken from the enum itself so this sentence cannot go stale. */
    public static String help() {
        return "known stores: " + Arrays.stream(Store.values())
                .map(s -> s.name().toLowerCase(java.util.Locale.ROOT))
                .collect(Collectors.joining(", "));
    }
}
