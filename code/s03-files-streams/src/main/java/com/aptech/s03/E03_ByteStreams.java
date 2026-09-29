package com.aptech.s03;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Example 3 — Byte streams: FileOutputStream writes raw bytes, FileInputStream reads them.
 * Also shows try-with-resources, which closes the stream for you.
 */
public class E03_ByteStreams {
    public static void main(String[] args) {
        new File("sandbox").mkdirs();
        File file = new File("sandbox/bytes.bin");

        // 1) WRITE: try-with-resources - the stream in ( ) is closed automatically at the end
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(65);                               // one byte: 65 is 'A'
            out.write(new byte[]{66, 67, 10});           // B, C and a new-line character
            out.write("Java".getBytes());                // the bytes of a String
        } catch (IOException e) {
            System.out.println("Could not write: " + e.getMessage());
        }
        System.out.println("File size: " + file.length() + " bytes");

        // 2) READ: one byte at a time until read() returns -1
        try (FileInputStream in = new FileInputStream(file)) {
            int b;
            StringBuilder numbers = new StringBuilder();
            while ((b = in.read()) != -1) {
                numbers.append(b).append(' ');
            }
            System.out.println("Bytes read: " + numbers);
        } catch (IOException e) {
            System.out.println("Could not read: " + e.getMessage());
        }

        // 3) COPY: read into a byte[] "bucket" and write the bucket out
        try (FileInputStream in = new FileInputStream(file);
             FileOutputStream out = new FileOutputStream("sandbox/bytes-copy.bin")) {
            byte[] bucket = new byte[4];
            int count;
            int trips = 0;
            while ((count = in.read(bucket)) != -1) {    // count = how many bytes landed in the bucket
                out.write(bucket, 0, count);             // write only those bytes
                trips++;
            }
            System.out.println("Copied in " + trips + " trips with a 4-byte bucket");
        } catch (IOException e) {
            System.out.println("Could not copy: " + e.getMessage());
        }

        // 4) What happens with a file that does not exist?
        try (FileInputStream in = new FileInputStream("sandbox/missing.bin")) {
            System.out.println("This line never runs: " + in.read());
        } catch (IOException e) {
            System.out.println("Caught " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
