package com.aptech.a5.config;

import com.aptech.a5.repository.RepositoryFactory.Store;

import java.nio.file.Path;
import java.util.Locale;

/**
 * R4 — the configuration, read once.   <<< WRITE THIS CLASS >>>
 *
 * <p>Three settings arrive as JVM properties, and every one of them has a default:
 *
 * <pre>
 *   -Dregistry.store=memory      MEMORY or H2          (default: memory)
 *   -Dregistry.locale=fr         any language tag      (default: en)
 *   -Dregistry.exportDir=sandbox where files are saved (default: sandbox)
 * </pre>
 *
 * <p>Read them with {@code System.getProperty(name, fallback)} and keep them in fields. The class
 * is a <b>singleton</b>: one private constructor, one {@code private static final} instance, and a
 * {@code static get()} that hands it back. Everything that needs the configuration calls
 * {@code AppConfig.get()} and gets the same object the last caller got — so the properties are
 * read exactly once, at the moment the class is first touched.
 *
 * <p>Two things worth being honest about, and the report asks you to be:
 *
 * <ul>
 *   <li>A singleton is a global variable with a nicer name. It is the right shape for a value
 *       that is genuinely read-only for the life of the process and needed almost everywhere —
 *       and the wrong shape for anything else.</li>
 *   <li>It makes testing harder, because a test cannot hand it a different configuration. That is
 *       why the store is also reachable through {@link com.aptech.a5.repository.RepositoryFactory}
 *       with an explicit argument: production code reads the config, a test says what it wants.</li>
 * </ul>
 *
 * <p>Do not add a method that prints an identity hash or a timestamp. Sample output that changes
 * every run is sample output nobody can check.
 */
public final class AppConfig {

    /**
     * TODO R4 — private, so nobody outside this class can make a second one.
     *
     * <p>Read the three properties here and keep them in fields. The {@code private static final}
     * instance that calls this constructor goes immediately above it.
     */
    private AppConfig() {
        // TODO R4 — System.getProperty("registry.store", "memory"), and the other two.
    }

    /**
     * TODO R4 — the instance everybody shares.
     *
     * <p>Returns the same object on every call, which is the whole claim. Make sure a test can
     * prove it: two calls, {@code ==}, true.
     */
    public static AppConfig get() {
        throw new UnsupportedOperationException("R4: AppConfig.get() not implemented");
    }

    /** The configured store, parsed from {@code registry.store}. */
    public Store studentStore() {
        // TODO R4 — RepositoryFactory.parse(...) already knows the valid names and the error message.
        throw new UnsupportedOperationException("R4: AppConfig.studentStore() not implemented");
    }

    /** The configured locale, from {@code registry.locale}. "en" means {@link Locale#ENGLISH}. */
    public Locale locale() {
        // TODO R4 — Locale.of("fr") and Locale.of("fr", "CA") both work; there is also Locale.forLanguageTag.
        throw new UnsupportedOperationException("R4: AppConfig.locale() not implemented");
    }

    /** Where exported files go, from {@code registry.exportDir}. */
    public Path exportDir() {
        // TODO R4 — Path.of(...) is the whole implementation.
        throw new UnsupportedOperationException("R4: AppConfig.exportDir() not implemented");
    }

    /** One readable line for the report, e.g. {@code AppConfig[store=MEMORY, locale=en, exportDir=sandbox]}. */
    @Override
    public String toString() {
        // TODO R4
        throw new UnsupportedOperationException("R4: AppConfig.toString() not implemented");
    }
}
