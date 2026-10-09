package com.aptech.s09.config;

import com.aptech.s09.repository.RepositoryFactory;

import java.nio.file.Path;
import java.util.Locale;

/**
 * The program's settings, in exactly one object.
 *
 * <p>This is the Singleton pattern: a class that guarantees it has one instance and hands
 * that instance to anyone who asks. Three things make it one —
 * <ol>
 *   <li>a {@code private} constructor, so nobody outside can call {@code new};</li>
 *   <li>a {@code static final} field holding the one instance;</li>
 *   <li>a {@code static} accessor, {@link #get()}, that returns it.</li>
 * </ol>
 *
 * <p><strong>Read Part 4 before you copy this.</strong> A singleton is a claim — "there is
 * one of these in this program, forever" — and it is a claim you have to be able to defend.
 * Here it is defensible: these values are read once from system properties at startup and
 * never change. In a class that holds a connection, or anything a test would want two of, the
 * same pattern becomes a problem.
 *
 * <p>The values come from system properties with a default, so the same compiled program can
 * run in two configurations without a rebuild:
 * <pre>
 *   java -Dregistry.store=h2 -Dregistry.locale=fr com.aptech.s09.E04_Singleton
 * </pre>
 */
public final class AppConfig {

    /**
     * The one instance. It is built when this class is first touched, which is why the
     * constructor below must not call anything that touches {@code AppConfig} back.
     */
    private static final AppConfig INSTANCE = new AppConfig();

    private final RepositoryFactory.Store studentStore;
    private final Locale locale;
    private final Path exportDir;

    private AppConfig() {
        this.studentStore = RepositoryFactory.parse(System.getProperty("registry.store", "memory"));
        this.locale = Locale.forLanguageTag(System.getProperty("registry.locale", "en"));
        this.exportDir = Path.of(System.getProperty("registry.exportDir", "sandbox"));
    }

    /** Hand out the one instance. Note there is no {@code new AppConfig()} anywhere else. */
    public static AppConfig get() {
        return INSTANCE;
    }

    public RepositoryFactory.Store studentStore() {
        return studentStore;
    }

    public Locale locale() {
        return locale;
    }

    public Path exportDir() {
        return exportDir;
    }

    @Override
    public String toString() {
        return "AppConfig[store=" + studentStore
                + ", locale=" + locale.toLanguageTag()
                + ", exportDir=" + exportDir + "]";
    }
}
