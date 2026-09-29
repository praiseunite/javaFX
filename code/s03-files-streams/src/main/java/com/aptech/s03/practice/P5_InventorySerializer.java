package com.aptech.s03.practice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TreeMap;

/** Practice 5 (challenge) — serialize a whole shop inventory (a TreeMap of Item objects) and restore it. */
public class P5_InventorySerializer {

    record Item(String name, int quantity, double price) implements Serializable { }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new File("sandbox").mkdirs();
        File file = new File("sandbox/inventory.ser");

        TreeMap<String, Item> stock = new TreeMap<>();
        stock.put("P01", new Item("Pen", 120, 150.0));
        stock.put("B07", new Item("Notebook", 40, 900.0));
        stock.put("C12", new Item("Calculator", 5, 12500.0));

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(stock);
        }

        TreeMap<String, Item> restored;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            TreeMap<String, Item> read = (TreeMap<String, Item>) in.readObject();
            restored = read;
        }

        double value = 0;
        for (Item item : restored.values()) {
            value += item.quantity() * item.price();
        }
        System.out.println("Restored " + restored.size() + " items: " + restored.keySet());
        System.out.println("Calculator: " + restored.get("C12"));
        System.out.println("Total stock value: N" + value);
        System.out.println("Equal to the original? " + restored.equals(stock));
    }
}
