package com.aptech.s09.model;

import java.math.BigDecimal;

/**
 * A course, as the registry knows it.
 *
 * <p>A {@code record} is the Java 16+ way to write a class whose whole job is to hold data.
 * The compiler generates the private final fields, the constructor, the accessors
 * ({@code id()}, not {@code getId()}), and {@code equals}, {@code hashCode} and
 * {@code toString} — all the code you would otherwise write by hand and get wrong once.
 *
 * <p>What it does <em>not</em> generate is validation. That is what the compact constructor
 * below is for: it runs before the fields are assigned and can refuse a bad object.
 */
public record Course(int id, String title, int credits, BigDecimal fee) {

    /** The compact constructor: no parameter list, and it assigns nothing. */
    public Course {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (credits <= 0) {
            throw new IllegalArgumentException("credits must be positive, got " + credits);
        }
        if (fee == null || fee.signum() < 0) {
            throw new IllegalArgumentException("fee must not be negative, got " + fee);
        }
    }

    /**
     * A static factory method — the same idea as {@code RepositoryFactory}, scaled down to
     * one class. It reads better than {@code new Course(1, "Java", 12, new BigDecimal("450.00"))}
     * and it can cache, or return a subclass, without changing a single caller.
     */
    public static Course of(int id, String title, int credits, String fee) {
        return new Course(id, title, credits, new BigDecimal(fee));
    }

    /** A one-line label for menus and headings. */
    public String label() {
        return title + " (" + credits + " credits)";
    }
}
