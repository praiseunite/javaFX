package com.aptech.a5.i18n;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * R7 — the same messages in two languages.   <<< WRITE THIS CLASS >>>
 *
 * <p>A thin wrapper around {@link ResourceBundle}. It is thin on purpose: everything interesting
 * happens in the two {@code .properties} files, and the whole of this class is the call that finds
 * them.
 *
 * <p>The one call that matters:
 *
 * <pre>
 *   ResourceBundle bundle = ResourceBundle.getBundle("Messages", locale);
 * </pre>
 *
 * <p>The base name {@code "Messages"} has no path and no extension — the JVM turns it into
 * {@code Messages_fr.properties} and so on, and it looks on the classpath. Keep {@link #BUNDLE_NAME}
 * as a constant so the string appears exactly once.
 *
 * <p>{@link #forLocale} returns a new {@code Messages} holding the bundle and the locale it was
 * asked for (keep both — they are different things, and {@link #answeringFile} needs the second).
 *
 * <p>{@link #get} returns {@code bundle.getString(key)}, and this class does NOT catch
 * {@code MissingResourceException}. A key that is in neither file is a bug in the program, and a
 * silently blank string is how that bug survives to production. {@link #format} is the same idea
 * with {@link MessageFormat} on top.
 */
public final class Messages {

    /** The base name. The JVM adds the locale and the .properties extension. */
    public static final String BUNDLE_NAME = "Messages";

    private Messages() {
        // TODO R7 — make this take the bundle and the locale, and keep both in fields.
    }

    /** TODO R7 — getBundle(BUNDLE_NAME, locale), then wrap it. */
    public static Messages forLocale(Locale locale) {
        throw new UnsupportedOperationException("R7: Messages.forLocale() not implemented");
    }

    /** TODO R7 — the string for this key, exactly as the properties file wrote it. */
    public String get(String key) {
        throw new UnsupportedOperationException("R7: Messages.get() not implemented");
    }

    /**
     * TODO R7 — the string with its {0}, {1} holes filled in.
     *
     * <p>Build the formatter with {@code new MessageFormat(get(key), locale)} — passing the locale
     * is what makes {1,date,long} print "1 August 2026" in English and "1 août 2026" in French
     * without you writing either by hand.
     */
    public String format(String key, Object... args) {
        throw new UnsupportedOperationException("R7: Messages.format() not implemented");
    }

    /**
     * TODO R7 — the file that actually answered, e.g. {@code "Messages_fr.properties"}.
     *
     * <p>Not the same as the locale that was asked for: ask for French Canadian and you get
     * {@code Messages_fr.properties}, because there is no {@code Messages_fr_CA.properties}. That
     * difference is the most interesting thing on the whole page, so the report prints it.
     */
    public String answeringFile() {
        throw new UnsupportedOperationException("R7: Messages.answeringFile() not implemented");
    }

    /** The locale this instance was asked for. */
    public Locale locale() {
        throw new UnsupportedOperationException("R7: Messages.locale() not implemented");
    }

    /**
     * GIVEN — a {@link LocalDate} as the {@link java.util.Date} that MessageFormat still wants.
     *
     * <p>This one line is worth reading, because it is genuinely surprising: the modern date API
     * cannot be used directly in a date placeholder. {@code MessageFormat} predates
     * {@code java.time} by a decade and takes {@code java.util.Date}, so
     * {@code {1,date,long}} needs this conversion. It is the only place in the assignment where
     * the old API is unavoidable, and now it is in exactly one method.
     */
    public static java.util.Date asDate(LocalDate date) {
        return java.sql.Date.valueOf(date);
    }
}
