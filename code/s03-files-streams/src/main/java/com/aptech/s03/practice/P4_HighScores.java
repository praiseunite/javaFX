package com.aptech.s03.practice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Practice 4 (medium) — save a high-score table in binary: first the COUNT, then each name + score. */
public class P4_HighScores {

    static void save(Map<String, Integer> scores, File file) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
            out.writeInt(scores.size());                 // write how many entries follow
            for (Map.Entry<String, Integer> e : scores.entrySet()) {
                out.writeUTF(e.getKey());
                out.writeInt(e.getValue());
            }
        }
    }

    static Map<String, Integer> load(File file) throws IOException {
        Map<String, Integer> scores = new LinkedHashMap<>();
        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            int count = in.readInt();                    // read the count first
            for (int i = 0; i < count; i++) {
                String name = in.readUTF();
                int score = in.readInt();
                scores.put(name, score);
            }
        }
        return scores;
    }

    public static void main(String[] args) throws IOException {
        new File("sandbox").mkdirs();
        File file = new File("sandbox/highscores.dat");
        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("Kemi", 9800);
        scores.put("Musa", 7200);
        scores.put("Ada", 6100);

        save(scores, file);
        System.out.println("Saved " + scores.size() + " scores in " + file.length() + " bytes");
        System.out.println("Loaded: " + load(file));
    }
}
