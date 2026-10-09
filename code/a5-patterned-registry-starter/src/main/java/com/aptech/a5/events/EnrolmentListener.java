package com.aptech.a5.events;

/**
 * Somebody who wants to know when an enrolment happens. GIVEN — do not change it.
 *
 * <p>The registry will hold a list of these and call each one in turn. It will not know what any
 * of them does, or how many there are, or whether there are none. R6 is one implementation.
 */
@FunctionalInterface
public interface EnrolmentListener {

    void onEnrolment(EnrolmentEvent event);

    default String name() {
        return "listener";
    }
}
