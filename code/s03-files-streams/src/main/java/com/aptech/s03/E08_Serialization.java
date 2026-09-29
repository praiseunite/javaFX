package com.aptech.s03;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Example 8 — Serialization: save WHOLE OBJECTS to a file and bring them back later.
 */
public class E08_Serialization {

    /** "implements Serializable" is Java's permission slip: objects of this class may be saved. */
    static class Account implements Serializable {
        private static final long serialVersionUID = 1L;   // the class's "version number"

        private final String owner;
        private final double balance;
        private final ArrayList<String> history = new ArrayList<>();   // ArrayList is Serializable
        private transient String pin;                      // transient = NEVER saved

        Account(String owner, double balance, String pin) {
            this.owner = owner;
            this.balance = balance;
            this.pin = pin;
        }

        @Override
        public String toString() {
            return owner + " balance=" + balance + " history=" + history + " pin=" + pin;
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new File("sandbox").mkdirs();
        File file = new File("sandbox/accounts.ser");

        List<Account> accounts = new ArrayList<>();
        Account ada = new Account("Ada", 2500.0, "1234");
        ada.history.add("deposit 500");
        accounts.add(ada);
        accounts.add(new Account("Ben", 90.5, "9999"));
        System.out.println("Before saving: " + accounts);

        // SERIALIZE: object -> bytes -> file
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(accounts);                     // the whole list, and everything inside it
        }
        System.out.println("Saved " + file.length() + " bytes");

        // DESERIALIZE: file -> bytes -> brand-new objects
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            List<Account> loaded = (List<Account>) in.readObject();   // readObject returns Object, so cast
            System.out.println("After loading: " + loaded);
            System.out.println("Same object as before? " + (loaded.get(0) == ada));
        }
    }
}
