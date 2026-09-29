package com.aptech.s02.lab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GUIDED LAB — a generic "lucky draw" bag.
 * Put in things of type T, then draw them out at random (each item can be drawn only once).
 */
public class Bag<T> {

    private final List<T> items = new ArrayList<>();   // STEP 2: storage for items of type T
    private final Random random;

    public Bag(long seed) {                            // a fixed seed makes the draw repeatable for testing
        this.random = new Random(seed);
    }

    public void add(T item) {                          // STEP 3: only accepts a T
        items.add(item);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** STEP 4: removes and returns a random item, or null if the bag is empty. */
    public T draw() {
        if (items.isEmpty()) {
            return null;
        }
        int index = random.nextInt(items.size());      // 0 .. size-1
        return items.remove(index);
    }

    /** STEP 6: a generic METHOD - moves every item from one bag into another bag of the same type. */
    public static <E> void pourInto(Bag<E> from, Bag<E> to) {
        while (!from.isEmpty()) {
            to.add(from.draw());
        }
    }
}
