package com.aptech.a2;

import java.io.Serializable;
import java.util.Objects;

/**
 * One student record. GIVEN to students complete — do not change it.
 * It implements Serializable so that whole Student objects can be saved with ObjectOutputStream (R6/R7).
 */
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String course;
    private final int score;

    public Student(String id, String name, String course, int score) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.score = score;
    }

    public String getId()     { return id; }
    public String getName()   { return name; }
    public String getCourse() { return course; }
    public int getScore()     { return score; }

    /** Two students are equal when all four fields are equal (used by SelfCheck to compare lists). */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student s = (Student) o;
        return score == s.score && id.equals(s.id) && name.equals(s.name) && course.equals(s.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, course, score);
    }

    @Override
    public String toString() {
        return String.format("%-5s %-10s %-7s %3d", id, name, course, score);
    }
}
