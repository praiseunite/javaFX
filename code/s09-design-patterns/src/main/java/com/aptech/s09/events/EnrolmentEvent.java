package com.aptech.s09.events;

import com.aptech.s09.model.Course;
import com.aptech.s09.model.Student;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * What just happened, in one object: the student, the course, the fee that was worked out,
 * the policy that worked it out, and the date.
 *
 * <p>A listener gets this and nothing else. That is what keeps the Observer pattern in Part 6
 * honest: the audit log cannot reach back into the registry and change something, because all
 * it was handed is an immutable record. If listeners were passed the registry itself, "notify
 * everyone" would quietly become "let everyone interfere".
 *
 * <p>Adding a field to this record is a change every listener can see but none is forced to
 * handle — the same reason {@code EnrolmentRequest} is an object rather than a parameter list.
 */
public record EnrolmentEvent(Student student, Course course, BigDecimal fee,
                             String policyName, LocalDate date, int instalments) {
}
