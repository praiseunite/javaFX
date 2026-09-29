package com.aptech.s03;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Example 1 — A stream is a sequence of data that flows one piece at a time.
 * Here the "source" is just an array in memory, so you can see exactly what read() does.
 */
public class E01_WhatIsAStream {
    public static void main(String[] args) throws IOException {
        byte[] source = {72, 105, 33};                 // the bytes for the letters H, i and !
        InputStream in = new ByteArrayInputStream(source);

        int b;
        while ((b = in.read()) != -1) {                // read ONE byte; -1 means "no more data"
            System.out.println("read() gave " + b + "  which is the character '" + (char) b + "'");
        }
        System.out.println("read() gave " + in.read() + "  -> the end of the stream");

        // Writing is the same idea in the other direction
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(74);                                 // J
        out.write(97);                                 // a
        out.write("va".getBytes());                    // a whole array of bytes at once
        System.out.println("Bytes written: " + out.size() + " -> as text: " + out);
    }
}
