package com.aptech.s09.events;

import java.util.ArrayList;
import java.util.List;

/**
 * A listener that would send an email, and instead remembers that it would have.
 *
 * <p>Every teaching project has one of these, and it is not a cop-out. A listener that really
 * sent mail would make the example fail without a network, take a second per enrolment, and
 * email a stranger when someone ran the practice exercises. A "stub" that records what it was
 * asked to do proves the wiring works and asserts nothing about the outside world.
 *
 * <p>The seam is the same one a real mailer would use: this class implements
 * {@link EnrolmentListener}, so swapping in a real {@code SmtpEmailListener} later changes one
 * line of the program that builds the registry — nowhere else.
 */
public final class EmailStub implements EnrolmentListener {

    private final List<String> sent = new ArrayList<>();

    @Override
    public void onEnrolment(EnrolmentEvent event) {
        sent.add(event.student().email()
                + "  \"Welcome to " + event.course().title() + " - fee " + event.fee() + "\"");
    }

    /** The messages that would have been sent, in order. */
    public List<String> sent() {
        return List.copyOf(sent);
    }

    @Override
    public String name() {
        return "email (stub)";
    }
}
