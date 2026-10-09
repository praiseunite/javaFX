package com.aptech.s09.view;

import com.aptech.s09.model.EnrolmentReceipt;
import com.aptech.s09.model.Student;

import java.util.List;

/**
 * The console view: turns the model into text and prints it.
 *
 * <p>Every format string in this project lives in this file. That is why a column width can
 * be changed without touching a repository, and why the same registry can be shown in a
 * JavaFX table by writing one more class that implements {@link RegistryView}.
 */
public final class ConsoleView implements RegistryView {

    /** The header that goes above {@link #row}. */
    public static final String HEADER = String.format("%-4s %-19s %-24s %-6s %s",
            "id", "name", "email", "grade", "status");

    @Override
    public void showStudents(List<Student> students) {
        System.out.println("  " + HEADER);
        System.out.println("  " + "-".repeat(68));
        for (Student s : students) {
            System.out.println("  " + row(s));
        }
        System.out.println("  " + students.size() + " student(s)");
    }

    @Override
    public void showReceipt(EnrolmentReceipt r) {
        System.out.println("  enrolled : " + r.student().name()
                + " (" + r.student().email() + ") on " + r.course().label());
        System.out.println("  fee      : " + r.fee() + "   [" + r.policyName() + "]");
        if (r.isSplit()) {
            System.out.println("  split    : " + r.instalments() + " x " + r.perInstalment());
        }
        System.out.println("  notified : " + r.notified() + " listener(s)");
    }

    @Override
    public void showMessage(String message) {
        System.out.println("  " + message);
    }

    /** One student as one line — the only place the column widths are written down. */
    public static String row(Student s) {
        return String.format("%-4d %-19s %-24s %-6s %s",
                s.id(), s.name(), s.email(), s.grade(), s.active() ? "active" : "inactive");
    }

    @Override
    public String name() {
        return "console view";
    }
}
