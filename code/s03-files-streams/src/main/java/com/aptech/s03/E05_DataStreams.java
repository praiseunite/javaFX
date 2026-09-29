package com.aptech.s03;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Example 5 — DataOutputStream / DataInputStream (the DataOutput and DataInput interfaces):
 * save Java values (int, double, boolean, String) in binary form and read them back.
 */
public class E05_DataStreams {
    public static void main(String[] args) throws IOException {
        new File("sandbox").mkdirs();
        File file = new File("sandbox/player.dat");

        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
            out.writeUTF("Ada");          // a String: 2 bytes for its length + 3 bytes of text
            out.writeInt(1500);           // an int: always 4 bytes
            out.writeDouble(87.5);        // a double: always 8 bytes
            out.writeBoolean(true);       // a boolean: 1 byte
        }
        System.out.println("File size: " + file.length() + " bytes (5 + 4 + 8 + 1)");

        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            String name = in.readUTF();   // read back in EXACTLY the same order
            int points = in.readInt();
            double accuracy = in.readDouble();
            boolean online = in.readBoolean();
            System.out.println(name + " has " + points + " points, " + accuracy + "% accuracy, online=" + online);
        }

        // The wrong order does not give an error - it gives nonsense!
        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            int wrong = in.readInt();      // reads the first 4 bytes of "Ada" as a number
            System.out.println("Reading in the wrong order gives: " + wrong);
        }
    }
}
