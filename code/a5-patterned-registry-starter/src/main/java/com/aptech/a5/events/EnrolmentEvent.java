package com.aptech.a5.events;

import com.aptech.a5.model.Course;
import com.aptech.a5.model.Student;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * What just happened, told to anybody who asked to be told. GIVEN — do not change it.
 *
 * <p>An event is a record because it is a fact: it has already happened, so nothing may change it
 * after the fact. Notice it carries plain values and not a {@code Connection}, a
 * {@code ResultSet} or the service itself — a listener that could reach back into the store
 * would defeat the point of announcing.
 */
public record EnrolmentEvent(Student student, Course course, BigDecimal fee,
                             String policyName, LocalDate date, int instalments) {

    public String describe() {
        return student.name() + " -> " + course.title() + " on " + date
                + " for " + fee + " (" + policyName + ")";
    }
}
