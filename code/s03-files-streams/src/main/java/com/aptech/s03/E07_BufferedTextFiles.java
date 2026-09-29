package com.aptech.s03;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Example 7 — The everyday way to handle text files: BufferedWriter / PrintWriter to write,
 * BufferedReader.readLine() to read line by line. Saves and loads a small CSV file.
 */
public class E07_BufferedTextFiles {
    public static void main(String[] args) throws IOException {
        new File("sandbox").mkdirs();
        File csv = new File("sandbox/students.csv");

        // 1) WRITE lines with BufferedWriter
        try (BufferedWriter out = new BufferedWriter(new FileWriter(csv))) {
            out.write("id,name,score");
            out.newLine();                               // the correct line ending for this computer
            out.write("S1,Ada,95");
            out.newLine();
            out.write("S2,Ben,78");
            out.newLine();
        }

        // 2) APPEND with PrintWriter, which has println and printf like System.out
        try (PrintWriter out = new PrintWriter(new FileWriter(csv, true))) {
            out.println("S3,Chi,88");
            out.printf("%s,%s,%d%n", "S4", "Dayo", 61);
        }

        // 3) READ line by line with BufferedReader
        List<String> names = new ArrayList<>();
        int total = 0;
        try (BufferedReader in = new BufferedReader(new FileReader(csv))) {
            String header = in.readLine();               // first line: the column names
            System.out.println("Header: " + header);
            String line;
            while ((line = in.readLine()) != null) {     // readLine() returns null at the end
                String[] parts = line.split(",");        // "S1,Ada,95" -> ["S1", "Ada", "95"]
                names.add(parts[1]);
                total += Integer.parseInt(parts[2]);
            }
        }
        System.out.println("Students: " + names);
        System.out.println("Average score: " + (double) total / names.size());
    }
}
