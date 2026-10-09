package com.aptech.s09.policy;

import com.aptech.s09.model.Course;
import com.aptech.s09.model.Student;

import java.time.LocalDate;

/**
 * Everything a fee policy is allowed to look at, in one object.
 *
 * <p>This is the small design move that makes the Strategy pattern in Part 5 work. A policy
 * could have taken three parameters — {@code feeFor(student, course, date)} — and adding a
 * fourth would have meant editing the interface and every implementation. One request object
 * means the next requirement ("students who pay by direct debit") adds a component here, and
 * the policies that do not care simply do not read it.
 *
 * <p>It is a record, so it is immutable: a policy cannot quietly change the request it was
 * given. A policy that could do that would be a policy you cannot test.
 */
public record EnrolmentRequest(Student student, Course course, LocalDate date, int instalments) {

    public EnrolmentRequest {
        if (student == null) throw new IllegalArgumentException("student is required");
        if (course == null) throw new IllegalArgumentException("course is required");
        if (date == null) throw new IllegalArgumentException("date is required");
        if (instalments < 1) {
            throw new IllegalArgumentException("instalments must be at least 1, got " + instalments);
        }
    }

    /** The common case: one payment, today. */
    public static EnrolmentRequest of(Student student, Course course, LocalDate date) {
        return new EnrolmentRequest(student, course, date, 1);
    }
}
