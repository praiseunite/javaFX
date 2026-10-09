package com.aptech.s09.i18n;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Every piece of text the registry shows a user, in the user's language.
 *
 * <p>Three things are happening in this class, and they are the three things
 * internationalization is:
 * <ol>
 *   <li><strong>The strings are not in the code.</strong> {@code get("app.title")} reads a
 *       {@code .properties} file. Changing the title is a text edit, not a build.</li>
 *   <li><strong>A missing translation falls back instead of failing.</strong> A key absent
 *       from {@code Messages_fr.properties} is found in {@code Messages.properties} — so a
 *       half-translated language is usable rather than broken.</li>
 *   <li><strong>Numbers, money and dates are formatted by the locale</strong>, not by
 *       {@code String.format}. {@code 405.00} is {@code $405.00} to a reader in
 *       {@code en_US} and {@code 405,00 €} to one in {@code fr_FR}; {@code 1 August 2026}
 *       is <em>1 août 2026</em>. Money needs the <em>country</em>, not just the language:
 *       {@code Locale.ENGLISH} and {@code Locale.FRENCH} carry no country, so Java refuses
 *       to guess a currency and prints the generic {@code ¤}. Example 8 shows both.</li>
 * </ol>
 *
 * <p>The resource bundle base name is {@code Messages}, and the JVM looks for
 * {@code Messages_<em>lang</em>_<em>COUNTRY</em>.properties} first, then
 * {@code Messages_<em>lang</em>.properties}, then {@code Messages.properties}. The last one
 * is required: it is the bundle everything else falls back to.
 */
public final class Messages {

    /** The base name of the bundle — the file name without the locale part. */
    public static final String BUNDLE_NAME = "Messages";

    /** The file that is the fallback for every language. */
    public static final String ROOT_FILE = BUNDLE_NAME + ".properties";

    private static final String MISSING_PREFIX = "!";
    private static final String MISSING_SUFFIX = "!";

    private final Locale locale;
    private final ResourceBundle bundle;

    private Messages(Locale locale) {
        this.locale = locale;
        this.bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
    }

    /** The messages for one locale. */
    public static Messages forLocale(Locale locale) {
        return new Messages(locale);
    }

    public Locale locale() {
        return locale;
    }

    // ------------------------------------------------------------------- lookup

    /**
     * The text for a key, or {@code !key!} when no bundle has it.
     *
     * <p>{@code ResourceBundle.getString} throws {@link MissingResourceException} for a key
     * that no bundle defines. Catching it here turns a crash into a visible marker: a screen
     * full of {@code !app.title!} is obviously unfinished, which is exactly what you want a
     * translator or a tester to see.
     */
    public String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return MISSING_PREFIX + key + MISSING_SUFFIX;
        }
    }

    /** True when this locale (or a bundle it falls back to) defines the key. */
    public boolean has(String key) {
        return bundle.containsKey(key);
    }

    /** The key set this locale ends up with — its own keys plus everything inherited. */
    public List<String> keys() {
        return bundle.keySet().stream().sorted().toList();
    }

    /**
     * A message with holes in it: {@code format("students.count", 8)}.
     *
     * <p>{@link MessageFormat} is what makes a translated sentence more than a lookup. Word
     * order differs between languages, so the translator decides where {@code {0}} goes, and
     * {@code ''} is how a real apostrophe is written in a pattern.
     */
    public String format(String key, Object... args) {
        return new MessageFormat(get(key), locale).format(args);
    }

    // ------------------------------------------------- which bundle answered, and why

    /** The locale of the bundle that actually answered — the proof, not the guess. */
    public Locale answeringBundle() {
        return bundle.getLocale();
    }

    /** That same answer as a file name, for the examples' output. */
    public String answeringFile() {
        Locale answered = bundle.getLocale();
        if (answered.getLanguage().isEmpty()) {
            return ROOT_FILE + " (root)";
        }
        return BUNDLE_NAME + "_" + answered + ".properties";
    }

    /**
     * The order the JVM tries bundles in, most specific first: the locale as given, then the
     * same language without its country, then the root file. Written out rather than
     * remembered, so Part 8's diagram and this method cannot disagree.
     */
    public static List<Locale> lookupChain(Locale locale) {
        List<Locale> chain = new ArrayList<>();
        chain.add(locale);
        if (!locale.getLanguage().isEmpty()) {
            Locale languageOnly = Locale.of(locale.getLanguage());
            // fr and fr_FR are different candidates, but asking for fr twice is noise.
            if (!locale.equals(languageOnly)) {
                chain.add(languageOnly);
            }
        }
        chain.add(Locale.ROOT);
        return chain;
    }

    /** A locale as text, with the root locale spelled out rather than left blank. */
    public static String describe(Locale locale) {
        return locale.getLanguage().isEmpty() ? "(root)" : locale.toString();
    }

    /** The lookup chain as text, for the examples' output. */
    public static List<String> describeChain(Locale locale) {
        return lookupChain(locale).stream().map(Messages::describe).toList();
    }

    // ------------------------------------------------------------------ formatting

    /** Money in the locale's own currency: {@code $405.00} / {@code 405,00 €}. */
    public String money(double amount) {
        return NumberFormat.getCurrencyInstance(locale).format(amount);
    }

    /** A plain number: {@code 1,234.5} / {@code 1 234,5}. */
    public String number(double amount) {
        return NumberFormat.getNumberInstance(locale).format(amount);
    }

    /** A fraction as a percentage: {@code 0.1} becomes {@code 10%}. */
    public String percent(double fraction) {
        return NumberFormat.getPercentInstance(locale).format(fraction);
    }

    /** A long date: {@code August 1, 2026} / {@code 1 août 2026}. */
    public String date(LocalDate day) {
        return DateFormat.getDateInstance(DateFormat.LONG, locale).format(asDate(day));
    }

    /** A short date: {@code 8/1/26} / {@code 01/08/2026}. */
    public String shortDate(LocalDate day) {
        return DateFormat.getDateInstance(DateFormat.SHORT, locale).format(asDate(day));
    }

    /**
     * {@link MessageFormat}'s date patterns want a {@link Date}. {@code java.time} is the
     * better API and the one to use in your own code, but a {@code LocalDate} is not a
     * {@code Date} and passing one to a {@code {0,date,long}} pattern fails at run time —
     * so the two worlds are bridged here, once.
     */
    public static Date asDate(LocalDate day) {
        return java.sql.Date.valueOf(day);
    }
}
