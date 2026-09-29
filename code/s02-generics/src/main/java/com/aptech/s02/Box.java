package com.aptech.s02;

/**
 * A generic class: a Box that can hold ONE item of any type T.
 * T is a "type parameter" - a placeholder that is filled in when you create a Box.
 */
public class Box<T> {

    private T item;                        // the item's type is whatever T turns out to be

    public void put(T item) {              // only accepts a T
        this.item = item;
    }

    public T get() {                       // gives back a T - no cast needed by the caller
        return item;
    }

    public boolean isEmpty() {
        return item == null;
    }

    @Override
    public String toString() {
        return "Box[" + item + "]";
    }
}
