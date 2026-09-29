package com.aptech.s03.practice;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/** Practice 2 (easy) — a reusable copy method that works for ANY file (text, image, video...). */
public class P2_FileCopier {

    static long copy(File source, File target) throws IOException {
        long total = 0;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(target))) {
            byte[] bucket = new byte[1024];
            int n;
            while ((n = in.read(bucket)) != -1) {
                out.write(bucket, 0, n);
                total += n;
            }
        }
        return total;
    }

    public static void main(String[] args) throws IOException {
        new File("sandbox").mkdirs();
        File original = new File("sandbox/photo.bin");
        try (FileOutputStream out = new FileOutputStream(original)) {
            for (int i = 0; i < 5000; i++) {
                out.write(i % 200);                      // pretend this is an image
            }
        }
        File copy = new File("sandbox/photo-copy.bin");
        long bytes = copy(original, copy);
        System.out.println("Copied " + bytes + " bytes. Same size? " + (original.length() == copy.length()));

        try {
            copy(new File("sandbox/nope.bin"), new File("sandbox/nope-copy.bin"));
        } catch (IOException e) {
            System.out.println("Error handled: " + e.getMessage());
        }
    }
}
