package com.aptech.s03;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Example 4 — Buffered byte streams: fewer trips to the disk.
 * A small "counting" stream shows how many times the real FILE is asked for data.
 */
public class E04_BufferedByteStreams {

    /** Wraps any InputStream and counts every call that reaches the file. */
    static class CountingInputStream extends FilterInputStream {
        int calls = 0;

        CountingInputStream(InputStream in) { super(in); }

        @Override public int read() throws IOException { calls++; return super.read(); }

        @Override public int read(byte[] b, int off, int len) throws IOException { calls++; return super.read(b, off, len); }
    }

    public static void main(String[] args) throws IOException {
        new File("sandbox").mkdirs();
        File file = new File("sandbox/100k.bin");

        // Write 100,000 bytes through a BufferedOutputStream
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            for (int i = 0; i < 100_000; i++) {
                out.write(i % 256);                      // goes into the buffer, not straight to disk
            }
        }                                                // close() flushes the last part of the buffer
        System.out.println("File size: " + file.length() + " bytes");

        // 1) NO buffer: every read() goes to the file
        CountingInputStream plain = new CountingInputStream(new FileInputStream(file));
        try (plain) {
            while (plain.read() != -1) { /* read every byte */ }
        }
        System.out.println("Unbuffered: the file was asked " + plain.calls + " times");

        // 2) WITH a buffer: BufferedInputStream fetches 8192 bytes at a time
        CountingInputStream counted = new CountingInputStream(new FileInputStream(file));
        try (BufferedInputStream buffered = new BufferedInputStream(counted)) {
            while (buffered.read() != -1) { /* read every byte - same code as above! */ }
        }
        System.out.println("Buffered:   the file was asked " + counted.calls + " times");
    }
}
