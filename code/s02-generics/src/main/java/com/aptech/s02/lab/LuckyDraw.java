package com.aptech.s02.lab;

/**
 * GUIDED LAB — using the generic Bag<T> with different types.
 */
public class LuckyDraw {
    public static void main(String[] args) {
        // STEP 5a: a Bag of Strings - the raffle
        Bag<String> raffle = new Bag<>(7);
        raffle.add("Ada");
        raffle.add("Ben");
        raffle.add("Chi");
        raffle.add("Dayo");
        // raffle.add(42);                              // would not compile: 42 is not a String
        String winner = raffle.draw();                  // no cast needed
        System.out.println("Raffle winner: " + winner + " (" + raffle.size() + " names left)");

        // STEP 5b: the SAME class, now a Bag of Integers - prize amounts
        Bag<Integer> prizes = new Bag<>(7);
        prizes.add(5000);
        prizes.add(10000);
        prizes.add(20000);
        int prize = prizes.draw();                      // unboxing Integer -> int
        System.out.println(winner + " wins N" + prize);

        // STEP 6: the generic method moves everything left into a second bag
        Bag<String> nextWeek = new Bag<>(7);
        Bag.pourInto(raffle, nextWeek);
        System.out.println("Moved to next week's raffle: " + nextWeek.size() + " names, this week's bag empty? " + raffle.isEmpty());

        // STEP 7: drawing from an empty bag is safe
        System.out.println("Draw from empty bag: " + raffle.draw());
    }
}
