package com.aptech.s09.events;

/**
 * Something that wants to know when a student enrols.
 *
 * <p>One method, so it is a {@link FunctionalInterface} and a listener can be a lambda:
 * <pre>
 *   registry.addListener(event -&gt; System.out.println("sms to " + event.student().name()));
 * </pre>
 *
 * <p>The direction of the dependency is the whole trick. The registry does not import the
 * audit log or the email sender — it imports <em>this</em>. New listener, no edit to the
 * registry: that is the Open/Closed principle, demonstrated rather than asserted.
 */
@FunctionalInterface
public interface EnrolmentListener {

    /** Called once per enrolment. Keep it short: the caller is waiting. */
    void onEnrolment(EnrolmentEvent event);

    /** A label for the examples' output. A lambda inherits this default. */
    default String name() {
        return "listener";
    }
}
