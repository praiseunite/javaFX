package com.aptech.a5.policy;

import com.aptech.a5.model.Course;
import com.aptech.a5.model.Student;

import java.time.LocalDate;

/**
 * Everything a fee rule is allowed to look at. GIVEN — do not change it.
 *
 * <p>A fee rule needs three things: who is enrolling, on what, and when. Passing them as one
 * object rather than as three parameters means a fourth rule that needs a fourth fact (a sibling's
 * name, a scholarship code) is a change here and nowhere else.
 */
public record EnrolmentRequest(Student student, Course course, LocalDate date, int instalments) {

    public EnrolmentRequest {
        if (student == null) throw new IllegalArgumentException("an enrolment needs a student");
        if (course == null) throw new IllegalArgumentException("an enrolment needs a course");
        if (date == null) throw new IllegalArgumentException("an enrolment needs a date");
        if (instalments < 1) throw new IllegalArgumentException("at least one instalment, got " + instalments);
    }

    /** One instalment — the ordinary case, so a caller can leave it out. */
    public static EnrolmentRequest of(Student student, Course course, LocalDate date) {
        return new EnrolmentRequest(student, course, date, 1);
    }
}
