package com.aptech.a1;

/**
 * One student record. This class is GIVEN to students complete — do not change it.
 */
public class Student {
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

    @Override
    public String toString() {
        return String.format("%-6s %-12s %-8s %3d", id, name, course, score);
    }
}
