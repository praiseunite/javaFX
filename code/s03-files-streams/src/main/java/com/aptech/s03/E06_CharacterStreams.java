package com.aptech.s03;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Example 6 — Character streams (Reader / Writer) work with TEXT characters, not raw bytes.
 */
public class E06_CharacterStreams {
    public static void main(String[] args) throws IOException {
        new File("sandbox").mkdirs();
        File file = new File("sandbox/price.txt");
        String text = "₦500 café";

        try (FileWriter writer = new FileWriter(file)) {     // Writer = characters out (UTF-8 by default)
            writer.write(text);
        }
        System.out.println("The text has " + text.length() + " characters");
        System.out.println("The file has " + file.length() + " bytes");

        // Read it back as CHARACTERS
        try (FileReader reader = new FileReader(file)) {
            int c;
            int count = 0;
            StringBuilder sb = new StringBuilder();
            while ((c = reader.read()) != -1) {             // read() returns one CHARACTER (or -1)
                sb.append((char) c);
                count++;
            }
            System.out.println("FileReader read " + count + " characters: " + sb);
        }

        // Read the same file as BYTES
        try (FileInputStream in = new FileInputStream(file)) {
            int count = 0;
            while (in.read() != -1) {
                count++;
            }
            System.out.println("FileInputStream read " + count + " bytes");
        }

        // FileWriter with append = true adds to the end instead of replacing the file
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(" - sold!");
        }
        try (FileReader reader = new FileReader(file)) {
            char[] buffer = new char[100];
            int n = reader.read(buffer);                     // read up to 100 chars at once
            System.out.println("After appending: " + new String(buffer, 0, n));
        }
    }
}
