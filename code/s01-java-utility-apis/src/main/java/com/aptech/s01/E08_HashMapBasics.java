package com.aptech.s01;

import java.util.HashMap;
import java.util.Map;

/**
 * Example 8 — A Map stores KEY → VALUE pairs, like a phone book.
 */
public class E08_HashMapBasics {
    public static void main(String[] args) {
        Map<String, String> phoneBook = new HashMap<>();

        phoneBook.put("Ada", "0801-111-2222");
        phoneBook.put("Ben", "0802-333-4444");
        phoneBook.put("Chi", "0803-555-6666");
        System.out.println("Phone book: " + phoneBook);

        System.out.println("Ben's number: " + phoneBook.get("Ben"));
        System.out.println("Zed's number: " + phoneBook.get("Zed"));          // missing key -> null
        System.out.println("Zed (safe):   " + phoneBook.getOrDefault("Zed", "unknown"));

        String old = phoneBook.put("Ada", "0809-999-0000");   // same key -> value is REPLACED
        System.out.println("Ada's old number was " + old);

        System.out.println("Has Chi? " + phoneBook.containsKey("Chi"));
        phoneBook.remove("Chi");
        System.out.println("Entries:  " + phoneBook.size());
        System.out.println("Final:    " + phoneBook);
    }
}
