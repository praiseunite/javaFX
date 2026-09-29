package com.aptech.s02;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Example 8 — Inheritance and generics: extending generic classes and implementing generic interfaces.
 */
public class E08_InheritanceWithGenerics {

    /** A generic interface: anything that can store and count items of type T. */
    interface Storage<T> {
        void store(T item);
        int count();
    }

    /** 1) The subclass stays generic: it passes its own T up to the parent. */
    static class LabelledBox<T> extends Box<T> {
        private final String label;

        LabelledBox(String label) { this.label = label; }

        @Override
        public String toString() { return label + ": " + super.toString(); }
    }

    /** 2) The subclass FIXES the type: an IntBox is a Box<Integer>, and is not generic itself. */
    static class IntBox extends Box<Integer> {
        void doubleIt() { put(get() * 2); }
    }

    /** 3) A class implementing a generic interface for one specific type. */
    static class NameList implements Storage<String> {
        private final List<String> names = new ArrayList<>();

        @Override public void store(String item) { names.add(item.trim()); }
        @Override public int count() { return names.size(); }
    }

    public static void main(String[] args) {
        LabelledBox<String> lunch = new LabelledBox<>("Lunch");
        lunch.put("Jollof rice");
        Box<String> asParent = lunch;               // a LabelledBox<String> IS-A Box<String>
        System.out.println(asParent);

        IntBox counter = new IntBox();
        counter.put(21);
        counter.doubleIt();
        System.out.println("IntBox holds " + counter.get());

        Storage<String> storage = new NameList();   // NameList IS-A Storage<String>
        storage.store("  Ada ");
        storage.store("Ben");
        System.out.println("Stored " + storage.count() + " names");

        // Same type argument -> normal inheritance works:
        ArrayList<String> arrayList = new ArrayList<>(List.of("a", "b"));
        List<String> list = arrayList;              // ArrayList<String> IS-A List<String>
        Collection<String> coll = list;             // List<String> IS-A Collection<String>
        System.out.println("Collection size: " + coll.size());

        // Different type argument -> NOT related, even though Integer extends Number:
        // Box<Number> numberBox = new Box<Integer>();   // COMPILE ERROR: incompatible types
    }
}
