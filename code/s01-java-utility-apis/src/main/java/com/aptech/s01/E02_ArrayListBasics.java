package com.aptech.s01;

import java.util.ArrayList;
import java.util.List;

/**
 * Example 2 — The ten ArrayList methods you will use every day.
 */
public class E02_ArrayListBasics {
    public static void main(String[] args) {
        List<String> cart = new ArrayList<>();

        cart.add("Bread");                 // add to the end
        cart.add("Milk");
        cart.add("Eggs");
        cart.add(1, "Butter");             // insert at index 1 (others shift right)
        System.out.println("Cart:        " + cart);

        System.out.println("First item:  " + cart.get(0));
        System.out.println("Item count:  " + cart.size());

        cart.set(2, "Oat Milk");           // replace the item at index 2
        System.out.println("After set:   " + cart);

        System.out.println("Has Eggs?    " + cart.contains("Eggs"));
        System.out.println("Where Eggs?  " + cart.indexOf("Eggs"));
        System.out.println("Where Rice?  " + cart.indexOf("Rice"));   // -1 means "not found"

        cart.remove(0);                    // remove by POSITION
        cart.remove("Eggs");               // remove by VALUE
        System.out.println("After remove:" + cart);

        cart.clear();
        System.out.println("Empty now?   " + cart.isEmpty());
    }
}
