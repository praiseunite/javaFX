package com.aptech.s09;

import com.aptech.s09.events.AuditLog;
import com.aptech.s09.events.EmailStub;
import com.aptech.s09.events.EnrolmentListener;
import com.aptech.s09.model.Course;
import com.aptech.s09.model.EnrolmentReceipt;
import com.aptech.s09.model.Student;
import com.aptech.s09.policy.EarlyBird;
import com.aptech.s09.repository.H2StudentRepository;
import com.aptech.s09.repository.InMemoryStudentRepository;
import com.aptech.s09.service.EnrolmentService;

import java.time.LocalDate;

/**
 * Example 6 — two listeners, and then a third, and the registry never hears about any of them.
 *
 * <p>Watch the sequence rather than the output. Enrol first with nobody listening: the
 * registry works. Attach two listeners, enrol again: they both hear about it. Attach a lambda
 * as a third — written in this file, in one line — and it hears about it too, with no edit to
 * {@code EnrolmentService}. Then detach everything and the registry is exactly as it was.
 */
public final class E06_Observer {

    public static void main(String[] args) throws Exception {
        Db.resetRegistry();

        Course java = Course.of(1, "Java Programming", 12, "450.00");
        LocalDate promo = LocalDate.of(2026, 8, 1);

        EnrolmentService registry = new EnrolmentService(
                new InMemoryStudentRepository(new H2StudentRepository().findAll()),
                new EarlyBird());

        System.out.println(Db.rule("First, with nobody listening"));
        EnrolmentReceipt quiet = registry.enrol(
                Student.unsaved("Nia Okoro", "nia@example.com", 1, null), java, promo);
        System.out.println("  enrolled : " + quiet.student().name() + " for " + quiet.fee());
        System.out.println("  notified : " + quiet.notified() + " listener(s)");
        System.out.println("  The registry still worked. Listening is optional by design.");

        AuditLog audit = new AuditLog();
        EmailStub email = new EmailStub();
        EnrolmentListener sms = event ->
                System.out.println("    [sms] " + event.student().name() + ": your fee is "
                        + event.fee() + " (" + event.policyName() + ")");

        System.out.println(Db.rule("Now two listeners, neither known to the registry"));
        registry.addListener(audit);
        registry.addListener(email);
        System.out.println("  attached : " + registry.listeners().stream()
                .map(EnrolmentListener::name).toList());

        registry.enrol(Student.unsaved("Kofi Mensah", "kofi@example.com", 1, null), java, promo, 3);
        registry.enrol(Student.unsaved("Lena Fischer", "lena@example.com", 1, null), java,
                LocalDate.of(2026, 9, 15));

        System.out.println();
        System.out.println("  the audit log kept:");
        for (String line : audit.lines()) {
            System.out.println("    " + line);
        }
        System.out.println("  the email stub would have sent:");
        for (String line : email.sent()) {
            System.out.println("    " + line);
        }

        System.out.println(Db.rule("A third listener, written in one line"));
        registry.addListener(sms);
        registry.enrol(Student.unsaved("Omar Haddad", "omar@example.com", 1, null), java, promo);

        System.out.println(Db.rule("Detach every listener"));
        registry.removeListener(audit);
        registry.removeListener(email);
        registry.removeListener(sms);
        EnrolmentReceipt silent = registry.enrol(
                Student.unsaved("Ruth Okafor", "ruth@example.com", 1, null), java, promo);
        System.out.println("  listeners : " + registry.listeners().size());
        System.out.println("  enrolled  : " + silent.student().name() + " for " + silent.fee());
        System.out.println("  No listener ran, and not one line of EnrolmentService changed.");
    }
}
