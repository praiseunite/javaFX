package com.aptech.s09.lab;

import com.aptech.s09.Db;
import com.aptech.s09.config.AppConfig;
import com.aptech.s09.controller.RegistryController;
import com.aptech.s09.events.AuditLog;
import com.aptech.s09.events.EmailStub;
import com.aptech.s09.i18n.Messages;
import com.aptech.s09.model.Course;
import com.aptech.s09.model.Student;
import com.aptech.s09.nio.RegistryExport;
import com.aptech.s09.policy.EarlyBird;
import com.aptech.s09.repository.RepositoryFactory;
import com.aptech.s09.repository.StudentRepository;
import com.aptech.s09.service.EnrolmentService;
import com.aptech.s09.view.ConsoleView;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The guided lab: every pattern in this session, wired together in one program.
 *
 * <p>Nothing here is new. Each step is one of the ten examples, one line long, in the order a
 * real start-up would do them — read the configuration, build the store, choose the rule,
 * attach the listeners, take the enrolments, show them, save them, and say good morning in
 * two languages.
 *
 * <p>Build it yourself in nine steps before you read this file. The steps are in the lab
 * section of the page, and each one names the example it comes from.
 */
public final class PatternedRegistry {

    private static final Path EXPORT = Path.of("sandbox", "lab-export");

    public static void main(String[] args) throws Exception {
        Db.resetRegistry();

        System.out.println(Db.rule("Step 1 - read the configuration (Singleton)"));
        System.out.println("  " + AppConfig.get());

        System.out.println(Db.rule("Step 2 - build a store from the configuration (Factory)"));
        StudentRepository repository = RepositoryFactory.createSeeded(AppConfig.get().studentStore());
        System.out.println("  " + repository.getClass().getSimpleName()
                + ", holding " + repository.count() + " student(s)");
        System.out.println("  H2 read those from the file. The in-memory store starts empty, so");
        System.out.println("  the factory filled it from H2 on the way out - and neither the code");
        System.out.println("  above nor the code below asked which one it got.");

        System.out.println(Db.rule("Step 3 - choose a fee rule (Strategy)"));
        EnrolmentService service = new EnrolmentService(repository, new EarlyBird());
        System.out.println("  policy now: " + service.policy().name());

        System.out.println(Db.rule("Step 4 - attach the listeners (Observer)"));
        AuditLog audit = new AuditLog();
        EmailStub email = new EmailStub();
        service.addListener(audit);
        service.addListener(email);
        System.out.println("  " + service.listeners().size()
                + " listeners attached, and the service does not know either of their names");

        System.out.println(Db.rule("Step 5 - take three enrolments"));
        Map<Integer, Course> catalogue = Map.of(
                1, Course.of(1, "Java Programming", 12, "450.00"),
                2, Course.of(2, "Database Fundamentals", 10, "380.00"));
        RegistryController controller = new RegistryController(service, catalogue, new ConsoleView());
        controller.enrol("Nia Okoro", "nia@example.com", 1, LocalDate.of(2026, 8, 1), 1);
        controller.enrol("Kofi Mensah", "kofi@example.com", 1, LocalDate.of(2026, 8, 1), 3);
        controller.enrol("Lena Fischer", "lena@example.com", 2, LocalDate.of(2026, 9, 15), 1);

        System.out.println(Db.rule("Step 6 - read the registry back through the view (MVC)"));
        controller.listStudents();

        System.out.println(Db.rule("Step 7 - what the listeners collected"));
        for (String line : audit.lines()) {
            System.out.println("    " + line);
        }
        System.out.println("  emails the stub would have sent: " + email.sent().size());
        for (String line : email.sent()) {
            System.out.println("    " + line);
        }

        System.out.println(Db.rule("Step 8 - export through java.nio.file"));
        RegistryExport export = new RegistryExport(EXPORT);
        export.deleteAll();
        Path file = export.export(repository.findAll());
        System.out.println("  wrote " + Files.size(file) + " bytes to " + file);
        List<Student> back = export.read();
        System.out.println("  read back " + back.size() + " students");
        System.out.println("  equal to what we exported : " + repository.findAll().equals(back));

        Path log = EXPORT.resolve("audit.log");
        Files.writeString(log, String.join("\n", audit.lines()) + "\n", StandardCharsets.UTF_8);
        System.out.println("  audit log : " + Files.size(log) + " bytes in " + log);

        System.out.println(Db.rule("Step 9 - the same registry, in French"));
        Messages french = Messages.forLocale(Locale.FRENCH);
        System.out.println("  answered by : " + french.answeringFile());
        System.out.println("  " + french.get("app.title") + " - " + french.get("app.subtitle"));
        System.out.println("  " + french.format("students.count", back.size()));
        System.out.println("  " + french.format("enrolment.instalments", 3, 135.0));
        System.out.println();
        System.out.println("  Not one line of the nine steps above asked which language was in use.");
    }
}
