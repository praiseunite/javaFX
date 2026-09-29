package com.aptech.a2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A2 — Student Record File Manager.   <<< THIS IS THE FILE YOU COMPLETE >>>
 *
 * Every method has a TODO. Replace the placeholder code with real code and run SelfCheck after each one.
 * ALWAYS use try-with-resources so every stream is closed, even when something goes wrong.
 *
 * Author: YOUR NAME HERE        Student ID: YOUR ID HERE
 */
public class StudentFileManager {

    public static final String CSV_HEADER = "id,name,course,score";

    private int lastSkipped = 0;

    /**
     * R1 — Saves the students as a CSV text file.
     * Line 1 is CSV_HEADER, then one line per student, e.g.  S1,Zara,JAVA,72
     * Call makeParentFolder(file) first so that missing folders are created.
     * Hint: BufferedWriter + FileWriter, write(...) then newLine()   (Example 7)
     */
    public void saveCsv(List<Student> students, File file) throws IOException {
        // TODO R1
    }

    /**
     * R2 — Loads a CSV file written by saveCsv and returns the students.
     *  - skip the header (the first line)
     *  - ignore blank lines completely
     *  - a line that does not have exactly 4 parts, or whose score is not a whole number,
     *    is a BAD line: skip it and add 1 to lastSkipped (reset lastSkipped to 0 at the start)
     *  - trim() spaces around each value
     * Hint: BufferedReader.readLine() in a while loop; line.split(","); Integer.parseInt inside try/catch
     */
    public List<Student> loadCsv(File file) throws IOException {
        // TODO R2
        return new ArrayList<>();
    }

    /** R3 — Returns how many bad lines the most recent loadCsv skipped. (Done for you.) */
    public int getLastSkipped() {
        return lastSkipped;
    }

    /**
     * R4 — Saves the students in BINARY with a DataOutputStream:
     * first writeInt(number of students), then for each student:
     * writeUTF(id), writeUTF(name), writeUTF(course), writeInt(score).
     * Tip: wrap a BufferedOutputStream inside the DataOutputStream for speed.   (Example 5)
     */
    public void saveBinary(List<Student> students, File file) throws IOException {
        // TODO R4
    }

    /**
     * R5 — Loads a file written by saveBinary. Read the count first, then loop that many times,
     * reading the fields in EXACTLY the same order they were written.
     */
    public List<Student> loadBinary(File file) throws IOException {
        // TODO R5
        return new ArrayList<>();
    }

    /**
     * R6 — Serializes the whole list with ObjectOutputStream.writeObject(...).
     * Tip: write new ArrayList<>(students) - ArrayList is guaranteed to be Serializable.   (Example 8)
     */
    public void saveObjects(List<Student> students, File file) throws IOException {
        // TODO R6
    }

    /**
     * R7 — Deserializes a list written by saveObjects. readObject() returns Object, so cast it to List<Student>.
     * readObject can throw ClassNotFoundException: catch it and throw new IOException("...", e) instead.
     */
    @SuppressWarnings("unchecked")
    public List<Student> loadObjects(File file) throws IOException {
        // TODO R7
        return new ArrayList<>();
    }

    /**
     * R8 — Copies ANY file using BufferedInputStream and BufferedOutputStream, with a byte[] "bucket".
     * Returns the total number of bytes copied. Create the target's folder first (makeParentFolder).
     */
    public long copyFile(File source, File target) throws IOException {
        // TODO R8
        return -1;
    }

    /**
     * R9 — Returns the names of the FILES (not folders) in 'folder' whose names end with 'extension',
     * sorted A-Z. If the folder does not exist, return an empty list (folder.list() returns null then).
     */
    public List<String> listFiles(File folder, String extension) {
        // TODO R9
        return new ArrayList<>();
    }

    /** Creates the folder a file will live in, if it doesn't exist yet. (Done for you.) */
    private static void makeParentFolder(File file) {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
    }
}
