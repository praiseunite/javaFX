package com.aptech.s09;

import com.aptech.s09.i18n.Messages;
import com.aptech.s09.model.Course;
import com.aptech.s09.model.Student;
import com.aptech.s09.repository.H2StudentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Example 8 — the same registry, in two languages, and the fallback chain that makes a
 * half-finished translation usable.
 *
 * <p>Two things to watch. First, the code is <em>identical</em> for both languages: nothing
 * in this file branches on which one is in use. Second, section 2 asks the French bundle for
 * a key the French file does not contain, and gets English — not an error, not a blank.
 */
public final class E08_I18n {

    public static void main(String[] args) throws Exception {
        Db.resetRegistry();

        // A real application takes this from the user's settings. It is pinned here so that
        // "there is no bundle for this language" falls back the same way on every machine.
        Locale.setDefault(Locale.ENGLISH);

        List<Student> students = new H2StudentRepository().findAll();
        Course java = Course.of(1, "Java Programming", 12, "450.00");
        Student ada = students.get(0);
        LocalDate date = LocalDate.of(2026, 8, 1);

        System.out.println(Db.rule("1. The same four calls, two locales"));
        for (Locale locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            Messages m = Messages.forLocale(locale);
            System.out.println("  asked for " + Messages.describe(locale)
                    + "  ->  answered by " + m.answeringFile());
            System.out.println("    " + m.get("app.title") + " - " + m.get("app.subtitle"));
            System.out.println("    " + m.format("students.count", students.size()));
            System.out.println("    " + m.format("enrolment.done", ada.name(),
                    Messages.asDate(date), 405.0));
            System.out.println("    " + m.get("menu.help"));
        }

        System.out.println(Db.rule("2. A key the French file does not have"));
        Messages fr = Messages.forLocale(Locale.FRENCH);
        System.out.println("  app.title      fr : " + fr.get("app.title"));
        System.out.println("  students.none  fr : " + fr.get("students.none"));
        System.out.println("  menu.quit      fr : " + fr.get("menu.quit"));
        System.out.println();
        System.out.println("  The last two are not in Messages_fr.properties. They came from the");
        System.out.println("  parent bundle, in English. That is the feature, not the bug:");
        System.out.println("  a language can ship at 80% translated and still be usable.");

        System.out.println(Db.rule("3. A key that no bundle has"));
        System.out.println("  registry.nope  fr : " + fr.get("registry.nope"));
        System.out.println("  registry.nope  en : "
                + Messages.forLocale(Locale.ENGLISH).get("registry.nope"));
        System.out.println();
        System.out.println("  getString() would have thrown MissingResourceException. Turning that");
        System.out.println("  into !key! means an unfinished screen is visible, not a crash.");

        System.out.println(Db.rule("4. The order the JVM tries, and which file won"));
        for (Locale locale : List.of(Locale.FRENCH, Locale.CANADA_FRENCH, Locale.GERMAN, Locale.ROOT)) {
            System.out.println("  " + pad(Messages.describe(locale)));
            System.out.println("    tries " + Messages.describeChain(locale));
            System.out.println("    won   " + Messages.forLocale(locale).answeringFile());
        }

        System.out.println(Db.rule("5. Numbers, money and dates are formatted, not concatenated"));
        for (Locale locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            Messages m = Messages.forLocale(locale);
            System.out.println("  " + Messages.describe(locale));
            System.out.println("    sentence : " + m.format("enrolment.done", ada.name(),
                    Messages.asDate(date), 405.0));
            System.out.println("    plan     : " + m.format("enrolment.instalments", 3, 157.5));
            System.out.println("    money    : " + m.money(405)
                    + "      number : " + m.number(1234567.5)
                    + "      percent : " + m.percent(0.1));
            System.out.println("    date     : " + m.date(date)
                    + "      short : " + m.shortDate(date));
        }

        System.out.println(Db.rule("6. Money needs a country, not just a language"));
        Messages en = Messages.forLocale(Locale.ENGLISH);
        Messages frFR = Messages.forLocale(Locale.FRANCE);
        System.out.println("  en     : " + en.money(405)
                + "   non-ASCII characters: " + codes(en.money(405)));
        System.out.println("  fr     : " + fr.money(405)
                + "   non-ASCII characters: " + codes(fr.money(405)));
        System.out.println("  fr_FR  : " + frFR.money(405)
                + "   non-ASCII characters: " + codes(frFR.money(405)));
        System.out.println();
        System.out.println("  Locale.FRENCH is \"fr\" - a language with no country attached, so");
        System.out.println("  nothing tells Java which currency to reach for and it prints");
        System.out.println("  U+00A4 instead of guessing. Locale.FRANCE is \"fr_FR\", and only");
        System.out.println("  there does it get as far as U+20AC. The two French lines also");
        System.out.println("  carry U+00A0: the convention puts a non-breaking space before");
        System.out.println("  the symbol. Formatting money by hand is a bug with a long fuse.");

        System.out.println(Db.rule("7. What a translator is handed"));
        System.out.println("  keys in " + Messages.ROOT_FILE + " : " + en.keys().size());
        System.out.println("  keys in Messages_fr.properties : " + fr.keys().size());
        System.out.println("  The counts match, because fr inherits everything it does not define.");
        System.out.println("  Two text files, no Java, and nothing to recompile.");
    }

    /** Left-aligned in a fixed column, so the four blocks line up. */
    private static String pad(String text) {
        return (text + " ".repeat(40)).substring(0, 40);
    }

    /** Every character that is not plain printable ASCII, as a code point. */
    private static String codes(String text) {
        StringBuilder out = new StringBuilder();
        text.codePoints().filter(cp -> cp < 0x20 || cp > 0x7E)
                .forEach(cp -> out.append(String.format("U+%04X ", cp)));
        return out.isEmpty() ? "(all ASCII)" : out.toString().trim();
    }
}
