package com.aptech.s01;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * Example 15 — How does Java sort YOUR objects? Comparable (natural order) vs Comparator (any order).
 */
public class E15_SortingObjects {

    // Comparable = "this class knows its own natural order"
    static class Student implements Comparable<Student> {
        private final String name;
        private final double gpa;

        Student(String name, double gpa) {
            this.name = name;
            this.gpa = gpa;
        }

        String getName() { return name; }
        double getGpa()  { return gpa; }

        @Override
        public int compareTo(Student other) {
            return this.name.compareTo(other.name);    // natural order = alphabetical by name
        }

        @Override
        public String toString() { return name + " " + gpa; }
    }

    public static void main(String[] args) {
        List<Student> list = new ArrayList<>();
        list.add(new Student("Musa", 3.1));
        list.add(new Student("Ada", 3.9));
        list.add(new Student("Kemi", 3.5));

        list.sort(null);                                           // null = use compareTo()
        System.out.println("By name:      " + list);

        list.sort(Comparator.comparingDouble(Student::getGpa));    // Comparator = a custom rule
        System.out.println("By GPA:       " + list);

        list.sort(Comparator.comparingDouble(Student::getGpa).reversed());
        System.out.println("By GPA desc:  " + list);

        // TreeSet needs an order too - here it uses compareTo()
        TreeSet<Student> roster = new TreeSet<>(list);
        System.out.println("TreeSet:      " + roster);
    }
}
