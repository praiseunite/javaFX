package com.aptech.s11.practice;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * P4 - Bullet 4, the Dictionary classes.
 *
 * A product may exist with no price yet. That single requirement is where Hashtable cannot
 * follow, and the workarounds people invented for it are worth seeing once.
 */
public class P4_NoPriceYet {

    public static void main(String[] args) {
        System.out.println("The requirement: a product may exist with no price set yet.");
        System.out.println();

        Hashtable<String, Integer> legacy = new Hashtable<>();
        legacy.put("BOLT", 250);

        System.out.println("1. Hashtable - store the unknown price directly");
        try {
            legacy.put("NUT", null);
        } catch (NullPointerException ex) {
            System.out.println("   legacy.put(\"NUT\", null) -> " + ex);
        }
        System.out.println();

        System.out.println("2. Workaround A: pick a number to mean \"no price\"");
        legacy.put("NUT", -1);
        System.out.println("   price(NUT) -> " + legacy.get("NUT") + "   (is -1 a price, or a flag?)");
        System.out.println("   Every reader now has to know the convention. One will forget.");
        System.out.println();

        System.out.println("3. Workaround B: presence in the table means priced");
        legacy.remove("NUT");
        String code = "NUT";
        Integer price = legacy.containsKey(code) ? legacy.get(code) : null;
        System.out.println("   price(NUT) -> " + price + "   (correct, and two lookups)");
        System.out.println();

        System.out.println("4. HashMap - the unknown price is a value like any other");
        Map<String, Integer> modern = new HashMap<>();
        modern.put("BOLT", 250);
        modern.put("NUT", null);
        System.out.println("   price(NUT)     -> " + modern.get("NUT"));
        System.out.println("   containsKey    -> " + modern.containsKey("NUT"));
        System.out.println("   price(WASHER)  -> " + modern.get("WASHER") + "   (a code never recorded)");
        System.out.println("   containsKey    -> " + modern.containsKey("WASHER"));
        System.out.println();
        System.out.println("   get() alone cannot tell \"no price yet\" from \"not in the catalogue\".");
        System.out.println("   containsKey() is what separates them - and it works on either class.");
    }
}
