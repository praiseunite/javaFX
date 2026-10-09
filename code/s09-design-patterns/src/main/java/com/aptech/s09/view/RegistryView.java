package com.aptech.s09.view;

import com.aptech.s09.model.EnrolmentReceipt;
import com.aptech.s09.model.Student;

import java.util.List;

/**
 * How the registry shows things. Three methods, and no decision anywhere in them.
 *
 * <p>A view is allowed to format, sort, colour and abbreviate. It is not allowed to decide
 * anything — no fee arithmetic, no validation, no "if the student is inactive don't show
 * them". The moment a view decides something, the next view decides it differently, and the
 * program has two businesses.
 *
 * <p>It is an interface rather than a class because this course is heading towards JavaFX in
 * a later session, where the implementation of this interface is a window and the methods
 * become {@code table.setItems(...)}. Nothing else in the program changes on that day — that
 * is the point of writing it down now.
 */
public interface RegistryView {

    /** Show a list of students. */
    void showStudents(List<Student> students);

    /** Show the result of one enrolment. */
    void showReceipt(EnrolmentReceipt receipt);

    /** Show a plain line of information or complaint. */
    void showMessage(String message);

    /** A label for the examples' output. */
    String name();
}
