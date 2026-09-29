package com.aptech.s03.practice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/** Practice 1 (easy) — count the lines, words and characters of a text file (like the "wc" tool). */
public class P1_LineWordCounter {
    public static void main(String[] args) throws IOException {
        new File("sandbox").mkdirs();
        File poem = new File("sandbox/poem.txt");
        try (PrintWriter out = new PrintWriter(new FileWriter(poem))) {
            out.println("Twinkle twinkle little star");
            out.println("How I wonder what you are");
            out.println("");
            out.println("Up above the world so high");
        }

        int lines = 0, words = 0, chars = 0;
        try (BufferedReader in = new BufferedReader(new FileReader(poem))) {
            String line;
            while ((line = in.readLine()) != null) {
                lines++;
                chars += line.length();
                if (!line.isBlank()) {
                    words += line.trim().split("\\s+").length;
                }
            }
        }
        System.out.println("Lines: " + lines + ", words: " + words + ", characters (without line breaks): " + chars);
    }
}
