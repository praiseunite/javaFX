package com.aptech.s06.drills;

import java.util.ArrayList;
import java.util.List;

/**
 * DRILL 2 — the Generics crash (PECS: Producer Extends, Consumer Super).
 *
 * The broken method is NOT here, because it does not compile — that is the entire bug.
 * It lives in snippets/Drill2_Broken.java.txt so it can be shown without breaking the build.
 *
 * This class is the same idea written correctly, so you can run it and see both halves:
 *
 *   ? extends Fruit  ->  the basket PRODUCES fruit for you. You may read. You may not add,
 *                        because the compiler does not know whether you were handed a
 *                        List&lt;Apple&gt; or a List&lt;Banana&gt;.
 *   ? super Apple    ->  the basket CONSUMES apples from you. You may add. A read comes
 *                        back as Object, because it might be a List&lt;Object&gt;.
 *
 * Mnemonic: PECS — Producer Extends, Consumer Super.
 */
public class D2_Pecs {

    static class Fruit {
        @Override public String toString() { return "Fruit"; }
    }

    static class Apple extends Fruit {
        @Override public String toString() { return "Apple"; }
    }

    static class Banana extends Fruit {
        @Override public String toString() { return "Banana"; }
    }

    /**
     * PRODUCER — reads every fruit out of the basket and hands them to the caller.
     * Accepts List&lt;Fruit&gt;, List&lt;Apple&gt;, List&lt;Banana&gt; ... any subtype.
     */
    static String readBasket(List<? extends Fruit> basket) {
        StringBuilder sb = new StringBuilder();
        for (Fruit fruit : basket) {
            sb.append(fruit).append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * CONSUMER — puts an apple into the basket.
     * Accepts List&lt;Apple&gt;, List&lt;Fruit&gt;, List&lt;Object&gt; ... any supertype.
     */
    static void addApple(List<? super Apple> basket) {
        basket.add(new Apple());
    }

    public static void main(String[] args) {
        List<Apple> appleBasket = new ArrayList<>(List.of(new Apple()));
        List<Fruit> fruitBasket = new ArrayList<>(List.of(new Banana()));
        List<Object> objectBasket = new ArrayList<>();

        System.out.println("Drill 2 - why ? extends may not add and ? super may");
        System.out.println();
        System.out.println("A method that only READS:  readBasket(List<? extends Fruit>)");
        System.out.println("  given a List<Apple>  -> " + readBasket(appleBasket));
        System.out.println("  given a List<Fruit>  -> " + readBasket(fruitBasket));
        System.out.println();
        System.out.println("A method that WRITES:      addApple(List<? super Apple>)");
        addApple(appleBasket);
        addApple(fruitBasket);
        addApple(objectBasket);
        System.out.println("  appleBasket  is now " + appleBasket);
        System.out.println("  fruitBasket  is now " + fruitBasket);
        System.out.println("  objectBasket is now " + objectBasket);
        System.out.println();
        System.out.println("All three baskets accepted the apple, and none of them is a List<Apple>");
        System.out.println("in particular - that is exactly what '? super Apple' promises.");
    }
}
