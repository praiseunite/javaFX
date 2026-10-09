package com.aptech.s09.practice;

import com.aptech.s09.Db;
import com.aptech.s09.i18n.Messages;
import com.aptech.s09.model.Student;
import com.aptech.s09.repository.H2StudentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Practice 3 — the same report in two languages, from the same code.
 *
 * <p>This is the exercise that makes internationalization click, because there is nothing to
 * it: the loop below runs twice, and the only thing that differs between the two blocks of
 * output is the {@link Locale} handed to {@link Messages}.
 *
 * <p>If you want to go further, add a key to both {@code .properties} files and use it here.
 * Adding a string is a two-file edit and no Java at all, which is the point.
 */
public final class P3_LocalisedReport {

    public static void main(String[] args) throws Exception {
        Db.resetRegistry();

        // Pinned so the "no bundle for this language" fallback is the same everywhere.
        Locale.setDefault(Locale.ENGLISH);

        List<Student> students = new H2StudentRepository().findAll();
        LocalDate date = LocalDate.of(2026, 8, 1);

        for (Locale locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            Messages m = Messages.forLocale(locale);
            System.out.println(Db.rule(Messages.describe(locale) + " - " + m.answeringFile()));
            System.out.println("  " + m.get("app.title"));
            System.out.println("  " + m.format("students.count", students.size()));
            System.out.println("  " + m.format("enrolment.instalments", 3, 135.0));
            System.out.println("  " + m.format("enrolment.done", "Nia Okoro",
                    Messages.asDate(date), 405.0));
            System.out.println("  " + m.get("menu.help"));
        }

        System.out.println(Db.rule("What changed between the two blocks"));
        System.out.println("  The Locale. That is the whole list.");
        System.out.println();
        System.out.println("  The strings are in Messages.properties and Messages_fr.properties,");
        System.out.println("  so the next language is a text file - and it can ship half-finished,");
        System.out.println("  because anything it does not define is inherited from the parent.");
    }
}
