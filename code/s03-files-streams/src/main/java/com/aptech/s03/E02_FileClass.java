package com.aptech.s03;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Example 2 — The File class describes a file or folder PATH. It does not read or write contents.
 */
public class E02_FileClass {
    public static void main(String[] args) throws IOException {
        File folder = new File("sandbox/e02");          // just a path - nothing is created yet
        System.out.println("Folder exists? " + folder.exists());
        System.out.println("mkdirs() created it? " + folder.mkdirs());   // makes sandbox AND e02

        File notes = new File(folder, "notes.txt");     // a file INSIDE that folder
        System.out.println("createNewFile(): " + notes.createNewFile());
        System.out.println("createNewFile() again: " + notes.createNewFile());  // already exists

        try (FileWriter w = new FileWriter(notes)) {    // put 5 characters in it (explained in Example 6)
            w.write("Hello");
        }

        System.out.println("getName():     " + notes.getName());
        System.out.println("getPath():     " + notes.getPath());
        System.out.println("isFile():      " + notes.isFile() + ", isDirectory(): " + notes.isDirectory());
        System.out.println("length():      " + notes.length() + " bytes");
        System.out.println("canRead():     " + notes.canRead() + ", canWrite(): " + notes.canWrite());

        new File(folder, "todo.txt").createNewFile();
        String[] names = folder.list();                 // names of everything inside the folder
        Arrays.sort(names);
        System.out.println("Folder contains: " + Arrays.toString(names));

        File renamed = new File(folder, "diary.txt");
        System.out.println("renameTo(): " + notes.renameTo(renamed) + " -> now exists? " + renamed.exists());

        for (File f : folder.listFiles()) {             // clean up so the example can run again
            f.delete();
        }
        System.out.println("delete() folder: " + folder.delete() + ", exists now? " + folder.exists());
    }
}
