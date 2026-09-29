package com.aptech.l2;

import java.util.ArrayList;
import java.util.List;

/**
 * R7 — A shelf with limited space that holds items of type T.   <<< YOU COMPLETE THIS FILE >>>
 * Example:  Shelf<String> s = new Shelf<>(2);  s.put("book"); s.put("lamp"); s.put("vase") -> false (full)
 */
public class Shelf<T> {

    private final int capacity;
    private final List<T> items = new ArrayList<>();

    public Shelf(int capacity) {
        this.capacity = capacity;
    }

    /** Adds the item if there is space and returns true. Returns false (and changes nothing) when full. */
    public boolean put(T item) {
        // TODO R7
        return false;
    }

    /** Removes and returns the most recently added item, or null if the shelf is empty. */
    public T takeLast() {
        // TODO R7
        return null;
    }

    public int size() {
        return items.size();
    }

    /** True when the number of items has reached the capacity. */
    public boolean isFull() {
        // TODO R7
        return capacity < 0;
    }

    /** Returns a COPY of the items, so code outside cannot change the shelf. Hint: new ArrayList<>(items) */
    public List<T> items() {
        // TODO R7
        return items;
    }
}
