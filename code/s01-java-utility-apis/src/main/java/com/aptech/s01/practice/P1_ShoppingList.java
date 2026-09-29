package com.aptech.s01.practice;

import java.util.ArrayList;
import java.util.List;

/** Practice 1 (easy) — Shopping list. */
public class P1_ShoppingList {
    public static void main(String[] args) {
        List<String> shopping = new ArrayList<>();
        shopping.add("Rice");
        shopping.add("Beans");
        shopping.add("Plantain");
        shopping.add("Tomatoes");
        shopping.add("Onions");
        System.out.println("List: " + shopping + " (" + shopping.size() + " items)");

        shopping.remove(1);                                   // remove the SECOND item (index 1)
        System.out.println("After removing item 2: " + shopping);

        System.out.println("Need Plantain? " + shopping.contains("Plantain"));
        System.out.println("Need Yam? " + shopping.contains("Yam"));

        for (int i = 0; i < shopping.size(); i++) {
            System.out.println((i + 1) + ". " + shopping.get(i));   // humans count from 1
        }
    }
}
