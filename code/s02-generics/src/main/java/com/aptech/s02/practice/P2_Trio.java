package com.aptech.s02.practice;

/** Practice 2 (easy) — a generic class holding THREE values of the same type. */
public class P2_Trio {

    static class Trio<T> {
        private final T a;
        private final T b;
        private final T c;

        Trio(T a, T b, T c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        T first()  { return a; }
        T last()   { return c; }

        /** Returns a new Trio in reverse order. */
        Trio<T> reversed() {
            return new Trio<>(c, b, a);
        }

        @Override
        public String toString() { return "[" + a + ", " + b + ", " + c + "]"; }
    }

    public static void main(String[] args) {
        Trio<String> podium = new Trio<>("Gold: Ada", "Silver: Ben", "Bronze: Chi");
        System.out.println(podium + " -> winner " + podium.first());
        System.out.println("Reversed: " + podium.reversed());

        Trio<Integer> dice = new Trio<>(6, 2, 4);
        int total = dice.first() + dice.last();
        System.out.println(dice + " -> first + last = " + total);
    }
}
