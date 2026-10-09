package com.aptech.a5.events;

import java.util.List;

/**
 * R6 — the listener that writes everything down.   <<< WRITE THIS CLASS >>>
 *
 * <p>Keep a {@code List<String>} of what you were told, in the order you were told it, and add one
 * line per event. Use {@link EnrolmentEvent#describe()} for the text, so the wording lives in one
 * place and a second listener cannot drift.
 *
 * <p>Two things this class is not allowed to do:
 *
 * <ul>
 *   <li><b>It does not print.</b> A listener that prints cannot be checked by a test, and a
 *       program with two listeners would print everything twice. It records; the report prints.</li>
 *   <li><b>It does not know who called it.</b> No parameter of type store, service or controller
 *       appears anywhere in this file. If it needs to know more, the event is what is missing a
 *       field — not this class that is missing a reference.</li>
 * </ul>
 *
 * <p>Return a copy from {@link #lines()}, not the list itself. A caller that can empty your audit
 * log is not auditing anything.
 */
public final class AuditLog implements EnrolmentListener {

    @Override
    public void onEnrolment(EnrolmentEvent event) {
        // TODO R6
        throw new UnsupportedOperationException("R6: AuditLog.onEnrolment() not implemented");
    }

    /** Everything recorded so far, oldest first. Never null. */
    public List<String> lines() {
        // TODO R6
        throw new UnsupportedOperationException("R6: AuditLog.lines() not implemented");
    }

    @Override
    public String name() {
        // TODO R6 — "audit log" is what the report shows.
        return "listener";
    }
}
