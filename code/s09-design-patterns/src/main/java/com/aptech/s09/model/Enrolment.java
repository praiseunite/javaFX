package com.aptech.s09.model;

import java.time.LocalDateTime;

/**
 * A student on a course, and whether the fee has been settled.
 *
 * <p>This is the join row that Session 8's transactions protected: writing an enrolment and
 * writing the matching payment are two inserts that must both succeed. Session 9 does not
 * repeat that lesson — it puts the row behind a repository so the transaction has one home.
 */
public record Enrolment(int id, int studentId, int courseId, boolean paid,
                        LocalDateTime enrolledAt) {

    /** A copy marked paid — used by the ledger part of the guided lab. */
    public Enrolment withPaid(boolean nowPaid) {
        return new Enrolment(id, studentId, courseId, nowPaid, enrolledAt);
    }
}
