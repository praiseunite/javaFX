package com.aptech.a2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A2 — Student Record File Manager: the menu program. GIVEN complete — the file work is in StudentFileManager.
 * All files are kept in the "data" folder inside the project.
 */
public class Main {

    private static final File DATA = new File("data");

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        StudentFileManager files = new StudentFileManager();
        List<Student> students = new ArrayList<>();

        System.out.println("=== Student Record File Manager ===");
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1) Add   2) List   3) Save CSV   4) Load CSV   5) Save binary   6) Load binary");
            System.out.println("7) Save objects   8) Load objects   9) Backup CSV   10) Show data files   0) Exit");
            System.out.print("Choose: ");
            String choice = in.hasNextLine() ? in.nextLine().trim() : "0";
            try {
                switch (choice) {
                    case "1" -> {
                        String id = ask(in, "ID: ");
                        String name = ask(in, "Name: ");
                        String course = ask(in, "Course: ").toUpperCase();
                        int score = Integer.parseInt(ask(in, "Score: "));
                        students.add(new Student(id, name, course, score));
                        System.out.println("Added. " + students.size() + " students in memory.");
                    }
                    case "2" -> {
                        if (students.isEmpty()) System.out.println("(no students in memory)");
                        students.forEach(s -> System.out.println("  " + s));
                    }
                    case "3" -> {
                        files.saveCsv(students, new File(DATA, "students.csv"));
                        System.out.println("Saved " + students.size() + " students to data/students.csv");
                    }
                    case "4" -> {
                        students = files.loadCsv(new File(DATA, "students.csv"));
                        System.out.println("Loaded " + students.size() + " students (skipped "
                                + files.getLastSkipped() + " bad lines)");
                    }
                    case "5" -> {
                        files.saveBinary(students, new File(DATA, "students.dat"));
                        System.out.println("Saved to data/students.dat");
                    }
                    case "6" -> {
                        students = files.loadBinary(new File(DATA, "students.dat"));
                        System.out.println("Loaded " + students.size() + " students from data/students.dat");
                    }
                    case "7" -> {
                        files.saveObjects(students, new File(DATA, "students.ser"));
                        System.out.println("Saved to data/students.ser");
                    }
                    case "8" -> {
                        students = files.loadObjects(new File(DATA, "students.ser"));
                        System.out.println("Loaded " + students.size() + " students from data/students.ser");
                    }
                    case "9" -> {
                        long bytes = files.copyFile(new File(DATA, "students.csv"), new File(DATA, "students-backup.csv"));
                        System.out.println("Backed up " + bytes + " bytes to data/students-backup.csv");
                    }
                    case "10" -> {
                        for (String ext : new String[]{".csv", ".dat", ".ser"}) {
                            System.out.println("  " + ext + " files: " + files.listFiles(DATA, ext));
                        }
                    }
                    case "0" -> running = false;
                    default -> System.out.println("Please choose a number from the menu.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: the score must be a whole number.");
            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    private static String ask(Scanner in, String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : "";
    }
}
