package com.aptech.a5;

import com.aptech.a5.config.AppConfig;
import com.aptech.a5.events.AuditLog;
import com.aptech.a5.events.EnrolmentEvent;
import com.aptech.a5.i18n.Messages;
import com.aptech.a5.model.Course;
import com.aptech.a5.model.Student;
import com.aptech.a5.nio.RegistryExport;
import com.aptech.a5.policy.EarlyBird;
import com.aptech.a5.policy.EnrolmentRequest;
import com.aptech.a5.policy.FeePolicy;
import com.aptech.a5.policy.FlatFee;
import com.aptech.a5.repository.H2StudentRepository;
import com.aptech.a5.repository.InMemoryStudentRepository;
import com.aptech.a5.repository.RepositoryFactory;
import com.aptech.a5.repository.StudentRepository;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * A5 SELF-CHECK — run this class and aim for all PASS.
 *
 * <p>One check per requirement, and each one is something the assignment actually asked for. Run
 * it after every requirement; a requirement that is not written yet reports
 * {@code not implemented yet} rather than stopping the program, so you always see where you are.
 *
 * <p>It uses a throwaway in-memory database and a {@code selfcheck-tmp} folder, so it never
 * touches your real registry and you can run it as often as you like.
 */
public class SelfCheck {

    private static int passed = 0;
    private static int failed = 0;

    private interface Check {
        void run() throws Exception;
    }

    private static final Map<Integer, Course> COURSES = Map.of(
            1, Course.of(1, "Java Programming", 12, "450.00"),
            2, Course.of(2, "Database Fundamentals", 10, "380.00"),
            3, Course.of(3, "Web Development", 8, "320.00"),
            4, Course.of(4, "Software Engineering", 14, "520.00"));

    public static void main(String[] args) throws Exception {
        System.out.println("=== A5 Self-Check ===\n");

        // Set BEFORE anything touches AppConfig: the singleton reads the properties once, at the
        // moment the class is first used, so a property set later has no effect at all.
        System.setProperty("registry.store", "h2");
        System.setProperty("registry.locale", "fr");
        System.setProperty("registry.exportDir", "selfcheck-tmp/export");

        run("R1 the in-memory store answers all six questions", SelfCheck::r1);
        run("R2 the database store answers the same six questions", SelfCheck::r2);
        run("R2 a null mark comes back null, not 0", SelfCheck::r2NullMark);
        run("R2 a search is a value, so an injection returns nothing", SelfCheck::r2Injection);
        run("R3 the factory picks the store by name", SelfCheck::r3);
        run("R4 the configuration is one instance, read once", SelfCheck::r4);
        run("R5 two fee rules, one call site", SelfCheck::r5);
        run("R6 the listener records what it was told", SelfCheck::r6);
        run("R7 the same messages in two languages", SelfCheck::r7);
        run("R8 export and read back is a round trip", SelfCheck::r8RoundTrip);
        run("R8 a student with no mark survives the round trip", SelfCheck::r8Nulls);
        run("R8 walk, glob and deleteAll", SelfCheck::r8Files);

        Files.deleteIfExists(Path.of("selfcheck-tmp", "export"));
        Files.deleteIfExists(Path.of("selfcheck-tmp", "roundtrip"));
        Files.deleteIfExists(Path.of("selfcheck-tmp"));

        System.out.println("\n" + passed + " passed, " + failed + " failed.");
        System.out.println(failed == 0
                ? "The registry is structured. Session 10 is going to be easy."
                : "Fix the FAILs above, then run SelfCheck again.");
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ---- R1 -----------------------------------------------------------------

    private static void r1() {
        InMemoryStudentRepository r = new InMemoryStudentRepository();
        check(r.count() == 0, "a new store should be empty, reported " + r.count());

        Student ada = r.save(Student.unsaved("Ada Lovelace", "ada@example.com", 1, new BigDecimal("88.50")));
        check(ada.id() != 0, "save() must return the stored student with the id the store chose");
        Student grace = r.save(Student.unsaved("Grace Hopper", "grace@example.com", 1, new BigDecimal("91.00")));
        check(grace.id() != ada.id(), "two inserts must not get the same id");
        check(r.count() == 2, "two saves should leave two students, found " + r.count());

        check(r.findById(ada.id()).isPresent(), "findById() lost a student that was just saved");
        check(r.findById(9999).isEmpty(), "findById() on a missing id must be an empty Optional");
        check(r.findByCourse(1).size() == 2, "both students are on course 1");
        check(r.findByCourse(2).isEmpty(), "nobody is on course 2");
        check(r.findByNameContaining("LOVELACE").size() == 1, "the search must not care about case");

        Student updated = r.save(ada.withMark(new BigDecimal("95.00")));
        check(updated.id() == ada.id(), "saving a student that already has an id must update, not insert");
        check(r.count() == 2, "an update must not add a student, count is now " + r.count());
        check(r.findById(ada.id()).orElseThrow().mark().compareTo(new BigDecimal("95.00")) == 0,
                "the mark was not updated");

        check(r.deleteById(grace.id()), "deleteById() should report true for a student that is there");
        check(!r.deleteById(9999), "deleteById() should report false for a missing id");
        check(r.count() == 1, "one delete should leave one student, found " + r.count());
        check(r.storeName() != null && !r.storeName().isBlank(), "storeName() must say something");
    }

    // ---- R2 -----------------------------------------------------------------

    private static void r2() {
        Db.reset(Db.MEM_URL);
        H2StudentRepository r = new H2StudentRepository(Db.MEM_URL);

        check(r.count() == 8, "the seeded registry holds 8 students, reported " + r.count());
        Student ada = r.findById(1).orElseThrow();
        check("Ada Lovelace".equals(ada.name()), "id 1 should be Ada Lovelace, got " + ada.name());
        check(ada.mark().compareTo(new BigDecimal("88.50")) == 0, "Ada's mark should be 88.50, got " + ada.mark());
        check(ada.courseId() == 1, "Ada is on course 1, got " + ada.courseId());
        check(ada.active(), "Ada is active");

        check(r.findById(9999).isEmpty(), "a missing id must be an empty Optional, not a throw");
        check(r.findAll().size() == 8, "findAll() should return all 8, got " + r.findAll().size());
        check(r.findByCourse(1).size() == 3, "course 1 has Ada, Grace and Margaret, got " + r.findByCourse(1).size());
        check(r.findByNameContaining("Hopper").size() == 1,
                "the search must not care about case, found " + r.findByNameContaining("Hopper").size());

        Student saved = r.save(Student.unsaved("Nia Okoro", "nia@example.com", 1, null));
        check(saved.id() > 8, "the database should have assigned a new id, got " + saved.id());
        check(r.count() == 9, "after an insert count() should be 9, got " + r.count());
        check(r.deleteById(saved.id()), "deleteById() should report true for a student that is there");
        check(!r.deleteById(9999), "deleteById() should report false for a missing id");
        check(r.count() == 8, "we are back to 8, got " + r.count());
        check(r.storeName() != null && !r.storeName().isBlank(), "storeName() must say something");
    }

    private static void r2NullMark() {
        Db.reset(Db.MEM_URL);
        H2StudentRepository r = new H2StudentRepository(Db.MEM_URL);

        Student saved = r.save(Student.unsaved("No Mark Yet", "nomark@example.com", null, null));
        Student back = r.findById(saved.id()).orElseThrow();
        check(back.mark() == null, "a null mark must come back null, not 0 - got " + back.mark());
        check(back.courseId() == null, "a null course must come back null - got " + back.courseId());
        check("-".equals(back.grade()), "a student with no mark has no grade, got " + back.grade());
    }

    private static void r2Injection() {
        Db.reset(Db.MEM_URL);
        H2StudentRepository r = new H2StudentRepository(Db.MEM_URL);

        // If the search glues this into the SQL, the WHERE becomes true for every row and the
        // table leaks. With a ? it is just a name that matches nothing.
        List<Student> leaked = r.findByNameContaining("' OR '1'='1");
        check(leaked.isEmpty(), "the search returned " + leaked.size()
                + " rows for an injection payload - the value is reaching the SQL, not a parameter");
    }

    // ---- R3 -----------------------------------------------------------------

    private static void r3() {
        check(RepositoryFactory.create(RepositoryFactory.Store.MEMORY) instanceof InMemoryStudentRepository,
                "Store.MEMORY must give an InMemoryStudentRepository");
        check(RepositoryFactory.create(RepositoryFactory.Store.H2) instanceof H2StudentRepository,
                "Store.H2 must give an H2StudentRepository");
        check(RepositoryFactory.create("memory") instanceof InMemoryStudentRepository,
                "the name should be accepted in any case");
        check(RepositoryFactory.create("  MemOrY  ") instanceof InMemoryStudentRepository,
                "surrounding spaces should not matter either");

        try {
            RepositoryFactory.create("mysql");
            check(false, "an unknown store name must throw, not quietly return something");
        } catch (IllegalArgumentException expected) {
            check(expected.getMessage().contains("memory") && expected.getMessage().contains("h2"),
                    "the message should list the valid names, got: " + expected.getMessage());
        }
    }

    // ---- R4 -----------------------------------------------------------------

    private static void r4() {
        AppConfig first = AppConfig.get();
        check(first == AppConfig.get(), "get() must return the same instance every time - it did not");
        check(first.studentStore() == RepositoryFactory.Store.H2,
                "registry.store was set to h2, so studentStore() should be H2, got " + first.studentStore());
        check("fr".equals(first.locale().getLanguage()),
                "registry.locale was set to fr, got " + first.locale());
        check(first.exportDir().toString().replace('\\', '/').endsWith("selfcheck-tmp/export"),
                "exportDir() should be the configured folder, got " + first.exportDir());
        check(first.toString().contains("H2") && first.toString().contains("fr"),
                "toString() should mention the store and the locale, got: " + first);
        check(RepositoryFactory.createDefault() instanceof H2StudentRepository,
                "createDefault() must follow the configuration, which says h2");
    }

    // ---- R5 -----------------------------------------------------------------

    private static void r5() {
        Course java = COURSES.get(1);
        Student ada = Student.unsaved("Ada Lovelace", "ada@example.com", 1, new BigDecimal("88.50"));
        LocalDate lastDay = LocalDate.of(2026, 8, 31);   // the discount applies ON this day
        LocalDate dayAfter = LocalDate.of(2026, 9, 1);   // and not on this one

        FeePolicy flat = new FlatFee();
        check(flat.feeFor(EnrolmentRequest.of(ada, java, lastDay)).compareTo(new BigDecimal("450.00")) == 0,
                "the flat fee is 450.00, got " + flat.feeFor(EnrolmentRequest.of(ada, java, lastDay)));
        check(flat.name().toLowerCase(Locale.ROOT).contains("flat"),
                "name() is still the default - the report needs to say what the rule is, got: " + flat.name());

        FeePolicy earlyBird = new EarlyBird();
        BigDecimal onTime = earlyBird.feeFor(EnrolmentRequest.of(ada, java, lastDay));
        check(onTime.compareTo(new BigDecimal("405.00")) == 0,
                "10% off 450.00 is 405.00, got " + onTime);
        BigDecimal tooLate = earlyBird.feeFor(EnrolmentRequest.of(ada, java, dayAfter));
        check(tooLate.compareTo(new BigDecimal("450.00")) == 0,
                "on 1 September the full fee applies, got " + tooLate);
        check(earlyBird.name().toLowerCase(Locale.ROOT).contains("early"),
                "name() is still the default, got: " + earlyBird.name());

        // One list, one loop, no if - which is the whole point of the strategy.
        List<FeePolicy> policies = List.of(flat, earlyBird);
        long distinct = policies.stream()
                .map(p -> p.feeFor(EnrolmentRequest.of(ada, java, lastDay)))
                .distinct()
                .count();
        check(distinct == 2, "the two rules should disagree before 1 September, they returned " + distinct + " answer(s)");
    }

    // ---- R6 -----------------------------------------------------------------

    private static void r6() {
        AuditLog log = new AuditLog();
        check(log.lines() != null, "lines() must never return null");
        check(log.lines().isEmpty(), "a fresh audit log should be empty");

        EnrolmentEvent event = new EnrolmentEvent(
                Student.unsaved("Ada Lovelace", "ada@example.com", 1, new BigDecimal("88.50")),
                COURSES.get(1), new BigDecimal("405.00"), "early bird (-10%)", LocalDate.of(2026, 8, 1), 1);

        log.onEnrolment(event);
        log.onEnrolment(event);
        check(log.lines().size() == 2, "two events should be two lines, got " + log.lines().size());

        String line = log.lines().get(0);
        check(line.contains("Ada Lovelace") && line.contains("Java Programming"),
                "a line should name the student and the course, got: " + line);
        check(!line.contains("null"), "the line should not contain the word null: " + line);
        check(!log.name().equals("listener"), "override name() so the report can say what this listener is");

        // A listener that hands out its own list can have it emptied by a caller. It must not.
        try {
            log.lines().clear();
        } catch (UnsupportedOperationException expected) {
            // An immutable copy - exactly right.
        }
        check(log.lines().size() == 2, "lines() handed out the live list - a caller just erased the audit log");
    }

    // ---- R7 -----------------------------------------------------------------

    private static void r7() {
        Messages english = Messages.forLocale(Locale.ENGLISH);
        check("Student Registry".equals(english.get("app.title")),
                "English app.title should be 'Student Registry', got: " + english.get("app.title"));
        check("Messages.properties".equals(english.answeringFile()),
                "English answers from the base file, got " + english.answeringFile());
        check(english.format("app.title").equals("Student Registry"),
                "format() with no holes should be the string itself");

        Messages french = Messages.forLocale(Locale.FRENCH);
        check("Messages_fr.properties".equals(french.answeringFile()),
                "French answers from the French file, got " + french.answeringFile());
        check(!french.get("app.title").equals(english.get("app.title")),
                "the French title is still the English one - translate the file");
        check(french.get("app.title").indexOf('é') >= 0 || french.get("app.title").indexOf('è') >= 0,
                "the accents did not survive - save Messages_fr.properties as UTF-8. Got: " + french.get("app.title"));
        check(!french.format("students.count", 8).equals(english.format("students.count", 8)),
                "students.count is still the English text - translate it too");
        check(french.format("students.count", 8).contains("8"),
                "the {0} hole was not filled: " + french.format("students.count", 8));

        // menu.quit is deliberately NOT translated, so that the fallback is visible. A key a
        // bundle does not define is inherited from its parent - no exception, no blank.
        check(french.get("menu.quit").equals(english.get("menu.quit")),
                "menu.quit should fall back to English. If you translated it, undo that - the "
                        + "self-check needs one key that proves inheritance works. Got: " + french.get("menu.quit"));

        // A locale with no file of its own quietly gets the base bundle.
        Messages german = Messages.forLocale(Locale.GERMAN);
        check("Messages.properties".equals(german.answeringFile()),
                "German has no file, so the base file should answer. Got " + german.answeringFile());

        // A key in NO file is a bug in the program, and must not be silently blank.
        try {
            french.get("registry.no.such.key");
            check(false, "a key in no file must throw MissingResourceException, not return something");
        } catch (MissingResourceException expected) {
            // correct
        }
    }

    // ---- R8 -----------------------------------------------------------------

    private static void r8RoundTrip() {
        Path dir = Path.of("selfcheck-tmp", "export");
        RegistryExport export = new RegistryExport(dir);
        export.deleteAll();
        check(!Files.exists(dir), "deleteAll() should leave no folder behind");
        check(export.read().isEmpty(), "reading a folder that is not there yet should be an empty list");

        Db.reset(Db.MEM_URL);
        List<Student> students = new H2StudentRepository(Db.MEM_URL).findAll();
        Path file = export.export(students);

        check(Files.exists(file), "export() should have written " + file);
        check("all.txt".equals(file.getFileName().toString()),
                "export() should return all.txt, got " + file.getFileName());

        List<Student> back = export.read();
        check(back.size() == students.size(),
                "wrote " + students.size() + " students and read back " + back.size());
        check(back.equals(students),
                "the round trip changed the data - the header is probably being parsed as a student");
        check(back.get(0).equals(students.get(0)), "student 0 is not equal after the round trip");
    }

    private static void r8Nulls() {
        Path dir = Path.of("selfcheck-tmp", "roundtrip");
        RegistryExport export = new RegistryExport(dir);
        export.deleteAll();

        List<Student> hand = List.of(
                new Student(1, "Ada Lovelace", "ada@example.com", 1, new BigDecimal("88.50"), true),
                new Student(2, "Nia Okoro", "nia@example.com", null, null, false));

        export.export(hand);
        check(export.read().equals(hand),
                "a null courseId and a null mark must come back as nulls, not 0 and not the word 'null'.\n"
                        + "      wrote: " + hand + "\n      read : " + export.read());
        export.deleteAll();
    }

    private static void r8Files() {
        Path dir = Path.of("selfcheck-tmp", "export");
        RegistryExport export = new RegistryExport(dir);
        export.deleteAll();

        Db.reset(Db.MEM_URL);
        StudentRepository repository = new H2StudentRepository(Db.MEM_URL);
        export.export(repository.findAll());

        check(export.txtFiles().size() == 8,
                "students/ should hold one file per student, found " + export.txtFiles().size());
        check(export.tree().size() == 9,
                "all.txt plus 8 student files is 9 files, found " + export.tree().size());

        // The walk must be sorted, or two runs print two different reports.
        List<Path> once = export.tree();
        List<Path> twice = export.tree();
        check(once.equals(twice), "tree() returned a different order the second time");

        export.deleteAll();
        check(!Files.exists(dir), "deleteAll() should remove the folder and everything under it");
        check(!Files.exists(dir.resolve(RegistryExport.STUDENTS_DIR)),
                "the students/ folder is still there");
    }

    // ------------------------------------------------------------------------

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void run(String name, Check check) {
        try {
            check.run();
            passed++;
            System.out.println("PASS  " + name);
        } catch (UnsupportedOperationException e) {
            failed++;
            System.out.println("FAIL  " + name + "\n      -> not implemented yet (" + e.getMessage() + ")");
        } catch (AssertionError e) {
            failed++;
            System.out.println("FAIL  " + name + "\n      -> " + e.getMessage());
        } catch (Exception e) {
            failed++;
            System.out.println("FAIL  " + name + "\n      -> crashed with " + e);
        }
    }
}
