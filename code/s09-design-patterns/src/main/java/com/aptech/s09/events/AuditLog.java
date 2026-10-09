package com.aptech.s09.events;

import java.util.ArrayList;
import java.util.List;

/**
 * A listener that writes a line about every enrolment and keeps the lines.
 *
 * <p>Keeping them in a list rather than printing straight away is what makes this testable:
 * a test can enrol three students and then assert on {@link #lines()} without capturing
 * anybody's console. Where the lines eventually go — a file, a table, a logging framework —
 * is a decision this class has not made yet, and does not have to.
 */
public final class AuditLog implements EnrolmentListener {

    private final List<String> lines = new ArrayList<>();

    @Override
    public void onEnrolment(EnrolmentEvent event) {
        lines.add(String.format("%s | %s enrolled on %s | %s | %s (%s)",
                event.date(),
                event.student().name(),
                event.course().title(),
                event.fee(),
                event.policyName(),
                event.instalments() == 1 ? "one payment" : event.instalments() + " instalments"));
    }

    /** The lines so far, as an unmodifiable copy. */
    public List<String> lines() {
        return List.copyOf(lines);
    }

    @Override
    public String name() {
        return "audit log";
    }
}
