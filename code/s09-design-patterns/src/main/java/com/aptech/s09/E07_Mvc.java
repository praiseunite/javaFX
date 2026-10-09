package com.aptech.s09;

import com.aptech.s09.controller.RegistryController;
import com.aptech.s09.model.Course;
import com.aptech.s09.model.EnrolmentReceipt;
import com.aptech.s09.model.Student;
import com.aptech.s09.policy.EarlyBird;
import com.aptech.s09.repository.H2StudentRepository;
import com.aptech.s09.service.EnrolmentService;
import com.aptech.s09.view.ConsoleView;
import com.aptech.s09.view.RegistryView;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Example 7 — the same model, two views, one controller.
 *
 * <p>MVC is not three folders. It is a rule about who is allowed to decide what: the model
 * decides, the view shows, the controller sequences. This program tests the rule by changing
 * the view half way through — section 4 — and watching everything else keep working.
 */
public final class E07_Mvc {

    public static void main(String[] args) throws Exception {
        Db.resetRegistry();

        Map<Integer, Course> catalogue = Map.of(
                1, Course.of(1, "Java Programming", 12, "450.00"),
                2, Course.of(2, "Database Fundamentals", 10, "380.00"),
                4, Course.of(4, "Software Engineering", 14, "520.00"));

        EnrolmentService service = new EnrolmentService(new H2StudentRepository(), new EarlyBird());
        RegistryController controller = new RegistryController(service, catalogue, new ConsoleView());

        System.out.println(Db.rule("1. a read request, shown through the console view"));
        controller.listStudentsOnCourse(1);

        System.out.println(Db.rule("2. a request that changes something"));
        controller.enrol("Nia Okoro", "nia@example.com", 1, LocalDate.of(2026, 8, 1), 3);

        System.out.println(Db.rule("3. a request naming a course that does not exist"));
        controller.enrol("Kofi Mensah", "kofi@example.com", 9, LocalDate.of(2026, 8, 1), 1);
        System.out.println("  (the controller refused before a student was created -");
        System.out.println("   validation belongs to the side that decides, not to the view)");

        System.out.println(Db.rule("4. same model, same controller, a different view"));
        controller.setView(new SummaryView());
        controller.listStudents();
        controller.enrol("Lena Fischer", "lena@example.com", 2, LocalDate.of(2026, 9, 15), 1);

        System.out.println(Db.rule("What it took to add a second view"));
        System.out.println("  One new class - the one at the bottom of this file.");
        System.out.println("  Student, Course, StudentRepository, EnrolmentService and");
        System.out.println("  RegistryController are unchanged, and were not recompiled differently.");
        System.out.println();
        System.out.println("  In Session 15 that new class is a JavaFX window, and this is the");
        System.out.println("  paragraph you will remember.");
    }

    /**
     * A view that shows totals instead of rows. It is written here, inside the example, to
     * keep the file count down — in a real project it would live in the view package with
     * {@code ConsoleView}.
     */
    static final class SummaryView implements RegistryView {

        @Override
        public void showStudents(List<Student> students) {
            long active = students.stream().filter(Student::active).count();
            System.out.println("  [summary] " + students.size() + " students, " + active + " active");
        }

        @Override
        public void showReceipt(EnrolmentReceipt receipt) {
            System.out.println("  [summary] receipt: " + receipt.student().name()
                    + " owes " + receipt.fee()
                    + (receipt.isSplit() ? " in " + receipt.instalments() + " parts" : ""));
        }

        @Override
        public void showMessage(String message) {
            System.out.println("  [summary] " + message);
        }

        @Override
        public String name() {
            return "summary view";
        }
    }
}
