package com.aptech.s02;

/**
 * A generic class with TWO type parameters: K (key / first) and V (value / second).
 */
public class Pair<K, V> {

    private final K first;
    private final V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst()  { return first; }
    public V getSecond() { return second; }

    /** Returns a NEW pair with the two parts swapped. Notice the types swap too: Pair<V, K>. */
    public Pair<V, K> swap() {
        return new Pair<>(second, first);
    }

    /** A static generic "factory" method - Java works out K and V from the arguments. */
    public static <K, V> Pair<K, V> of(K first, V second) {
        return new Pair<>(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
