package com.aptech.s02.practice;

/** Practice 3 (medium) — a BOUNDED generic method: count elements greater than a given one. */
public class P3_CountGreater {

    static <T extends Comparable<T>> int countGreaterThan(T[] array, T limit) {
        int count = 0;
        for (T element : array) {
            if (element.compareTo(limit) > 0) {       // compareTo is allowed because of the bound
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Integer[] scores = {45, 88, 67, 91, 50};
        System.out.println("Scores above 60: " + countGreaterThan(scores, 60));

        String[] words = {"apple", "mango", "kiwi", "banana"};
        System.out.println("Words after \"kiwi\" in the dictionary: " + countGreaterThan(words, "kiwi"));
    }
}
