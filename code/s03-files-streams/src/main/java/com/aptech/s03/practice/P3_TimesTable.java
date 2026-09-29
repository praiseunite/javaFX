package com.aptech.s03.practice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/** Practice 3 (medium) — write a formatted times table to a file with PrintWriter, then read it back. */
public class P3_TimesTable {
    public static void main(String[] args) throws IOException {
        new File("sandbox").mkdirs();
        File file = new File("sandbox/table-7.txt");
        int n = 7;

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.printf("Times table for %d%n", n);
            for (int i = 1; i <= 5; i++) {
                out.printf("%2d x %d = %3d%n", i, n, i * n);    // %2d = a number in 2 spaces, right-aligned
            }
        }

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("| " + line);
            }
        }
    }
}
