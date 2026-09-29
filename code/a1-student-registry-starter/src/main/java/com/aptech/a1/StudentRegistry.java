package com.aptech.a1;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A1 — Student Registry.   <<< THIS IS THE FILE YOU COMPLETE >>>
 *
 * Every method below has a TODO. Replace the placeholder "return" line with real code.
 * Run SelfCheck after each method to see your progress (PASS / FAIL).
 *
 * Author: YOUR NAME HERE        Student ID: YOUR ID HERE
 */
public class StudentRegistry {

    // Stores every student. Key = student ID, value = the Student object.
    // LinkedHashMap because: IDs must be unique (Map keys are unique) AND we want
    // to remember the order students registered in.
    private final Map<String, Student> studentsById = new LinkedHashMap<>();

    // The lab waiting list holds student IDs. First to join = first admitted (FIFO).
    private final Queue<String> labWaitingList = new ArrayDeque<>();

    /**
     * R1 — Adds a student.
     * Returns true if added. Returns false (and changes nothing) if the ID is already used.
     * Hint: studentsById.containsKey(...) and studentsById.put(key, value)
     */
    public boolean register(Student s) {
        // TODO R1
        return false;
    }

    /**
     * R2 — Returns all students in the order they registered.
     * Hint: studentsById.values() gives you the students; wrap them in a new ArrayList<>(...)
     */
    public List<Student> allInOrder() {
        // TODO R2
        return new ArrayList<>();
    }

    /**
     * R3 — Returns all students sorted A→Z by name.
     * IMPORTANT: sort a COPY — the stored registration order must not change.
     * Hint: make a new ArrayList from the values, then copy.sort(Comparator.comparing(Student::getName))
     */
    public List<Student> sortedByName() {
        // TODO R3
        return new ArrayList<>();
    }

    /**
     * R4 — Returns the student with this ID, or null if there is none.
     * Hint: one line. What does Map.get() return for a missing key?
     */
    public Student findById(String id) {
        // TODO R4
        return null;
    }

    /**
     * R5 — Removes the student with this ID. Returns true if someone was removed, false if not found.
     * Also take them off the lab waiting list if they are on it.
     * Hint: Map.remove(key) returns the removed value, or null. Queue has remove(Object) too.
     */
    public boolean remove(String id) {
        // TODO R5
        return false;
    }

    /**
     * R6 — Returns every course that has at least one student: sorted, no duplicates.
     * Hint: which Set is always sorted? Loop over the students and add each course.
     */
    public Set<String> courses() {
        // TODO R6
        return new TreeSet<>();
    }

    /**
     * R7 — Returns how many students are in each course, sorted by course name.
     * Example: {JAVA=2, PYTHON=1, SQL=2}
     * Hint: a TreeMap<String, Integer> and the getOrDefault(key, 0) + 1 counting pattern.
     */
    public Map<String, Integer> countPerCourse() {
        // TODO R7
        return new TreeMap<>();
    }

    /**
     * R8 — Puts a registered student at the BACK of the lab waiting list.
     * Returns false if the ID is not registered, or if the student is already waiting.
     * Hint: containsKey on the map, contains on the queue, then offer(...)
     */
    public boolean joinWaitingList(String id) {
        // TODO R8
        return false;
    }

    /**
     * R9 — Removes and returns the student at the FRONT of the waiting list.
     * Returns null if nobody is waiting (it must NOT crash).
     * Hint: poll() returns null on an empty queue; remove() would throw an exception.
     */
    public Student admitNext() {
        // TODO R9
        return null;
    }

    /** R9 helper (already done for you) — the IDs currently waiting, front first. */
    public List<String> waitingList() {
        return new ArrayList<>(labWaitingList);
    }

    /**
     * R10 — Returns the n highest scores, highest first. Example: [95, 88, 72]
     * If there are fewer than n students, return all their scores (highest first).
     * Hints:
     *   1. Copy every score into an int[] (its length = number of students).
     *   2. Arrays.sort(...) sorts from LOWEST to HIGHEST.
     *   3. Arrays.copyOfRange(array, from, to) gives you the last few (the biggest).
     *   4. Reverse that small array so the highest comes first.
     */
    public int[] topScores(int n) {
        // TODO R10
        return new int[0];
    }

    /** Number of registered students (already done for you). */
    public int size() {
        return studentsById.size();
    }
}
