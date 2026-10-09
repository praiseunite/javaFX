package com.aptech.a5;

import com.aptech.a5.config.AppConfig;
import com.aptech.a5.events.AuditLog;
import com.aptech.a5.events.EnrolmentEvent;
import com.aptech.a5.events.EnrolmentListener;
import com.aptech.a5.i18n.Messages;
import com.aptech.a5.model.Course;
import com.aptech.a5.model.Student;
import com.aptech.a5.nio.RegistryExport;
import com.aptech.a5.policy.EarlyBird;
import com.aptech.a5.policy.EnrolmentRequest;
import com.aptech.a5.policy.FeePolicy;
import com.aptech.a5.policy.FlatFee;
import com.aptech.a5.repository.RepositoryFactory;
import com.aptech.a5.repository.StudentRepository;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * GIVEN — the program that uses everything you write. Do not change it.
 *
 * <p>Run it after each requirement. Until a requirement is done, its step prints
 * {@code -- not implemented yet} and the program carries on to the next one, so you can see your
 * progress rather than a stack trace.
 *
 * <p>Read the order of the nine steps; they are the session in miniature. The configuration
 * decides the store, the store is built by the factory, the policy is chosen at the call site, the
 * listener is attached to something that knows nothing about it, and the report is printed in two
 * languages from one piece of code.
 */
public final class Main {

    /** A step that is allowed to fail without ending the program. */
    private interface Step {
        void run() throws Exception;
    }

    /** The catalogue is fixed data in this assignment; a real one would be its own repository. */
    private static final Map<Integer, Course> COURSES = Map.of(
            1, Course.of(1, "Java Programming", 12, "450.00"),
            2, Course.of(2, "Database Fundamentals", 10, "380.00"),
            3, Course.of(3, "Web Development", 8, "320.00"),
            4, Course.of(4, "Software Engineering", 14, "520.00"));

    private StudentRepository repository;
    private Registry registry;
    private AuditLog audit;
    private RegistryExport export;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("=== A5 - the patterned registry ===");
        Db.reset();

        step("1. the configuration (Singleton)", () -> {
            System.out.println("  " + AppConfig.get());
            System.out.println("  the same object twice? " + (AppConfig.get() == AppConfig.get()));
        });

        step("2. the store (Factory)", () -> {
            System.out.println("  " + repository().getClass().getSimpleName()
                    + " holds " + repository().count() + " student(s)");
            System.out.println("  storeName() says: " + repository().storeName());
        });

        step("3. the fee rules (Strategy)", () -> {
            EnrolmentRequest probe = EnrolmentRequest.of(
                    Student.unsaved("Probe", "probe@example.com", 1, null),
                    course(1), LocalDate.of(2026, 8, 1));
            for (FeePolicy policy : List.of(new FlatFee(), new EarlyBird())) {
                System.out.printf("  %-42s %9s%n", policy.name(), policy.feeFor(probe));
            }
        });

        step("4. the listener (Observer)", () -> {
            audit = new AuditLog();
            registry().addListener(audit);
            System.out.println("  1 listener attached: " + audit.name());
        });

        step("5. three enrolments", () -> {
            registry().enrol("Nia Okoro", "nia@example.com", 1, LocalDate.of(2026, 8, 1), 1);
            registry().enrol("Kofi Mensah", "kofi@example.com", 1, LocalDate.of(2026, 8, 1), 3);
            registry().enrol("Lena Fischer", "lena@example.com", 2, LocalDate.of(2026, 9, 15), 1);
        });

        step("6. the registry, read back through the store", () -> {
            System.out.printf("  %-4s %-19s %-24s %-6s %s%n", "id", "name", "email", "grade", "status");
            System.out.println("  " + "-".repeat(68));
            for (Student s : repository().findAll()) {
                System.out.println("  " + s);
            }
            System.out.println("  " + repository().findAll().size() + " students on file");
        });

        step("7. what the listener collected", () -> {
            for (String line : audit.lines()) {
                System.out.println("    " + line);
            }
            System.out.println("  " + audit.lines().size() + " lines recorded - by a class the "
                    + "registry has never heard of");
        });

        step("8. export through java.nio.file", () -> {
            export = new RegistryExport(AppConfig.get().exportDir());
            export.deleteAll();
            Path file = export.export(repository().findAll());
            System.out.println("  wrote " + java.nio.file.Files.size(file) + " bytes to " + file);
            List<Student> back = export.read();
            System.out.println("  read back " + back.size() + " students");
            System.out.println("  equal to what we exported : " + repository().findAll().equals(back));
            System.out.println("  files under the export folder : " + export.tree().size());
            System.out.println("  student files found by glob  : " + export.txtFiles().size());
        });

        step("9. the same registry, in French", () -> {
            Messages french = Messages.forLocale(Locale.FRENCH);
            System.out.println("  answered by : " + french.answeringFile());
            System.out.println("  " + french.get("app.title") + " - " + french.get("app.subtitle"));
            System.out.println("  " + french.format("students.count", repository().count()));
            System.out.println("  " + french.format("enrolment.instalments", 3, 135.00));
        });

        System.out.println();
        System.out.println("Run SelfCheck for the requirement-by-requirement result.");
    }

    // ------------------------------------------------------------------------

    /** Built on first use, so a requirement that is not finished does not stop the earlier ones. */
    private StudentRepository repository() {
        if (repository == null) {
            repository = RepositoryFactory.createDefault();
        }
        return repository;
    }

    private Registry registry() {
        if (registry == null) {
            registry = new Registry(repository(), new EarlyBird());
        }
        return registry;
    }

    /**
     * The Observer's subject, in twelve lines: it holds the store, one policy and a list of
     * listeners, and it knows nothing whatsoever about what any listener does. Notice there is no
     * {@code if (listener instanceof ...)} to be found — and that a list with nothing in it is
     * not a special case that needs an {@code if} either.
     */
    private static final class Registry {

        private final StudentRepository repository;
        private final FeePolicy policy;
        private final List<EnrolmentListener> listeners = new ArrayList<>();

        Registry(StudentRepository repository, FeePolicy policy) {
            this.repository = repository;
            this.policy = policy;
        }

        void addListener(EnrolmentListener listener) {
            listeners.add(listener);
        }

        Student enrol(String name, String email, Integer courseId, LocalDate date, int instalments) {
            Student saved = repository.save(Student.unsaved(name, email, courseId, null));
            Course course = course(courseId);
            BigDecimal fee = policy.feeFor(new EnrolmentRequest(saved, course, date, instalments));
            EnrolmentEvent event = new EnrolmentEvent(saved, course, fee, policy.name(), date, instalments);
            for (EnrolmentListener listener : listeners) {
                listener.onEnrolment(event);
            }
            return saved;
        }

        private Course course(Integer courseId) {
            return COURSES.get(courseId);
        }
    }

    /** The same four courses the database was seeded with. */
    private static Course course(Integer courseId) {
        return COURSES.get(courseId);
    }

    /** Prints the rule, runs the step, and turns a missing implementation into one line. */
    private static void step(String title, Step body) {
        System.out.println(Db.rule(title));
        try {
            body.run();
        } catch (UnsupportedOperationException e) {
            System.out.println("  -- not implemented yet: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  -- failed: " + e);
        }
    }
}
