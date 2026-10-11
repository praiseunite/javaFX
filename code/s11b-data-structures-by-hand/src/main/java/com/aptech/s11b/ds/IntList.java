package com.aptech.s11b.ds;

/**
 * A growable list of ints, built by hand.
 *
 * This is the whole of ArrayList's core idea and nothing else: an array that is usually bigger
 * than the data, plus a count of how much of it is used. When the array fills up, a bigger one
 * is allocated and everything is copied across. That copy is the only expensive operation, and
 * doubling is what makes it rare.
 *
 * The point of writing it is that nothing here is magic. ArrayList.get(i) is O(1) because it is
 * the line {@code return data[index];} below - one array read, whatever the size.
 *
 * {@code copies} counts the element copies performed by growth, so the cost of add() can be
 * measured by counting rather than by timing. A count is deterministic; a millisecond reading
 * is noise plus truth.
 */
public class IntList {

    private int[] data;
    private int size;
    private int copies;
    private long reads;

    /** A list with room for four, which is where the doubling table on the page starts. */
    public IntList() {
        this(4);
    }

    public IntList(int initialCapacity) {
        data = new int[Math.max(1, initialCapacity)];
    }

    /** How many elements are actually stored. */
    public int size() {
        return size;
    }

    /** How much room the backing array has. Always >= size. */
    public int capacity() {
        return data.length;
    }

    /** Total element copies performed by every grow() so far. */
    public int copies() {
        return copies;
    }

    /** How many array reads get() has performed. E02 compares this with ArrayList's count. */
    public long reads() {
        return reads;
    }

    /** Appends a value, growing the array first if it is full. */
    public void add(int value) {
        if (size == data.length) {
            grow();
        }
        data[size++] = value;
    }

    /** One array read. This is the line that makes get() O(1). */
    public int get(int index) {
        checkIndex(index);
        reads++;
        return data[index];
    }

    public void set(int index, int value) {
        checkIndex(index);
        data[index] = value;
    }

    /** Up to size reads: the worst case, and the reason contains() is O(n). */
    public int indexOf(int value) {
        for (int i = 0; i < size; i++) {
            if (data[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public boolean contains(int value) {
        return indexOf(value) >= 0;
    }

    /** Removes one element and shifts the rest left, which is the O(n) part. */
    public int removeAt(int index) {
        checkIndex(index);
        int removed = data[index];
        System.arraycopy(data, index + 1, data, index, size - index - 1);
        data[--size] = 0;
        return removed;
    }

    /**
     * The whole trick, in six lines: allocate twice the room, copy the live elements, throw the
     * old array away. Every element is copied once per growth, so the total for n adds is about
     * n + n/2 + n/4 + ... which is less than 2n - that is why add() is called O(1) on average.
     */
    private void grow() {
        int[] bigger = new int[data.length * 2];
        System.arraycopy(data, 0, bigger, 0, size);
        copies += size;
        data = bigger;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
    }
}
