package com.aptech.s02;

/**
 * Example 3 — Creating (instantiating) objects of our generic Box class.
 */
public class E03_BoxDemo {
    public static void main(String[] args) {
        Box<String> nameBox = new Box<>();        // T becomes String
        nameBox.put("Ada");
        String name = nameBox.get();              // returns String - no cast
        System.out.println(nameBox + " holds a String of length " + name.length());

        Box<Integer> scoreBox = new Box<>();      // T becomes Integer
        scoreBox.put(95);                         // autoboxing: int 95 -> Integer
        int score = scoreBox.get();               // unboxing: Integer -> int
        System.out.println(scoreBox + " holds a score, plus bonus = " + (score + 5));

        Box<Box<String>> giftBox = new Box<>();   // a box inside a box - T is Box<String>
        giftBox.put(nameBox);
        System.out.println("Gift box: " + giftBox + ", inner item: " + giftBox.get().get());

        Box<Double> emptyBox = new Box<>();
        System.out.println("Is the Double box empty? " + emptyBox.isEmpty());
        // scoreBox.put("ninety");                // COMPILE ERROR: String cannot be converted to Integer
    }
}
