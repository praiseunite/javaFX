package com.aptech.s03.practice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

/** Practice 6 (challenge) — analyse a log file: count lines per level and write a report file. */
public class P6_LogAnalyzer {
    public static void main(String[] args) throws IOException {
        new File("sandbox").mkdirs();
        File log = new File("sandbox/app.log");
        try (PrintWriter out = new PrintWriter(new FileWriter(log))) {
            out.println("INFO  app started");
            out.println("WARN  disk 80% full");
            out.println("INFO  user ada logged in");
            out.println("ERROR database timeout");
            out.println("INFO  user ben logged in");
            out.println("ERROR database timeout");
        }

        Map<String, Integer> perLevel = new TreeMap<>();        // Session 1 counting pattern
        String longest = "";
        try (BufferedReader in = new BufferedReader(new FileReader(log))) {
            String line;
            while ((line = in.readLine()) != null) {
                String level = line.split("\\s+")[0];
                perLevel.put(level, perLevel.getOrDefault(level, 0) + 1);
                if (line.length() > longest.length()) {
                    longest = line;
                }
            }
        }

        File report = new File("sandbox/report.txt");
        try (PrintWriter out = new PrintWriter(new FileWriter(report))) {
            out.println("LOG REPORT");
            for (Map.Entry<String, Integer> e : perLevel.entrySet()) {
                out.printf("%-6s %d%n", e.getKey(), e.getValue());
            }
            out.println("Longest line: " + longest);
        }

        try (BufferedReader in = new BufferedReader(new FileReader(report))) {
            in.lines().forEach(System.out::println);            // BufferedReader can also give a Stream of lines
        }
    }
}
