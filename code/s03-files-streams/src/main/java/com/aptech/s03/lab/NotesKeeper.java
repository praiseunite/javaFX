package com.aptech.s03.lab;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * GUIDED LAB — Notes Keeper: save notes to a text file, read them back, count them, and back them up.
 */
public class NotesKeeper {

    private final File file;

    public NotesKeeper(File file) {
        this.file = file;
    }

    /** STEP 2 — make sure the folder exists and start with an empty notes file. */
    public void reset() throws IOException {
        file.getParentFile().mkdirs();
        try (FileWriter w = new FileWriter(file)) {       // opening WITHOUT append empties the file
            w.write("");
        }
    }

    /** STEP 3 — add one note at the END of the file (append mode). */
    public void addNote(String note) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file, true))) {
            out.write(note);
            out.newLine();
        }
    }

    /** STEP 4 — read every line back into a List. */
    public List<String> readAll() throws IOException {
        List<String> notes = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                notes.add(line);
            }
        }
        return notes;
    }

    /** STEP 5 — count all the words in all the notes. */
    public int countWords() throws IOException {
        int words = 0;
        for (String note : readAll()) {
            if (!note.isBlank()) {
                words += note.trim().split("\\s+").length;   // split on one or more spaces
            }
        }
        return words;
    }

    /** STEP 6 — copy the file byte by byte (with buffers) and return how many bytes were copied. */
    public long backupTo(File backup) throws IOException {
        long copied = 0;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(backup))) {
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
                copied++;
            }
        }
        return copied;
    }

    public static void main(String[] args) throws IOException {
        NotesKeeper keeper = new NotesKeeper(new File("sandbox/notes/diary.txt"));
        keeper.reset();

        keeper.addNote("Learned about byte and character streams");
        keeper.addNote("Buffered streams make fewer trips to the disk");
        keeper.addNote("Remember: close your streams with try-with-resources");

        List<String> notes = keeper.readAll();
        for (int i = 0; i < notes.size(); i++) {
            System.out.println((i + 1) + ". " + notes.get(i));
        }
        System.out.println("Notes: " + notes.size() + ", words: " + keeper.countWords());

        File backup = new File("sandbox/notes/diary-backup.txt");
        long copied = keeper.backupTo(backup);
        System.out.println("Backed up " + copied + " bytes; sizes match? "
                + (backup.length() == new File("sandbox/notes/diary.txt").length()));
    }
}
