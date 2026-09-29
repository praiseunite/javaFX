package com.aptech.a1;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * A1 — Student Registry: the menu program. This file is GIVEN to students complete.
 * All the collection work happens in StudentRegistry, which is the part you write.
 */
public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        StudentRegistry registry = new StudentRegistry();

        System.out.println("=== Aptech Student Registry ===");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = in.hasNextLine() ? in.nextLine().trim() : "0";
            switch (choice) {
                case "1" -> {
                    String id = ask(in, "ID: ");
                    String name = ask(in, "Name: ");
                    String course = ask(in, "Course: ").toUpperCase();
                    int score = askInt(in, "Score (0-100): ");
                    boolean ok = registry.register(new Student(id, name, course, score));
                    System.out.println(ok ? "Registered " + name + "." : "Error: ID " + id + " is already registered.");
                }
                case "2" -> printStudents("All students (registration order):", registry.allInOrder());
                case "3" -> printStudents("All students (A-Z by name):", registry.sortedByName());
                case "4" -> {
                    Student s = registry.findById(ask(in, "ID to find: "));
                    System.out.println(s == null ? "No student with that ID." : "Found: " + s);
                }
                case "5" -> {
                    String id = ask(in, "ID to remove: ");
                    System.out.println(registry.remove(id) ? "Removed " + id + "." : "No student with that ID.");
                }
                case "6" -> System.out.println("Courses offered: " + registry.courses());
                case "7" -> {
                    System.out.println("Students per course:");
                    for (Map.Entry<String, Integer> e : registry.countPerCourse().entrySet()) {
                        System.out.println("  " + e.getKey() + ": " + e.getValue());
                    }
                }
                case "8" -> {
                    String id = ask(in, "ID to join lab waiting list: ");
                    System.out.println(registry.joinWaitingList(id)
                            ? "Added. Waiting list: " + registry.waitingList()
                            : "Cannot add: unknown ID or already waiting.");
                }
                case "9" -> {
                    Student s = registry.admitNext();
                    System.out.println(s == null ? "Nobody is waiting." : "Admitted to lab: " + s.getName()
                            + ". Still waiting: " + registry.waitingList());
                }
                case "10" -> System.out.println("Top 3 scores: " + Arrays.toString(registry.topScores(3)));
                case "0" -> running = false;
                default -> System.out.println("Please choose a number from the menu.");
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1) Register   2) List all   3) List A-Z   4) Find   5) Remove");
        System.out.println("6) Courses    7) Per course 8) Join lab   9) Admit   10) Top 3   0) Exit");
        System.out.print("Choose: ");
    }

    private static void printStudents(String title, List<Student> list) {
        System.out.println(title);
        if (list.isEmpty()) {
            System.out.println("  (no students yet)");
        }
        for (Student s : list) {
            System.out.println("  " + s);
        }
    }

    private static String ask(Scanner in, String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : "";
    }

    private static int askInt(Scanner in, String prompt) {
        while (true) {
            String text = ask(in, prompt);
            try {
                int value = Integer.parseInt(text);
                if (value >= 0 && value <= 100) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // fall through to the message below
            }
            System.out.println("Please type a whole number from 0 to 100.");
        }
    }
}
