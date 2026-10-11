package com.aptech.s11;

import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

/**
 * E00 — framing: "legacy" is not a synonym for "deprecated".
 *
 * The session page claims that the six legacy classes are old but NOT deprecated. Rather than
 * assert that, this program proves it: java.lang.Deprecated is declared with
 * {@code @Retention(RUNTIME)}, so reflection can see it. If the JDK ever deprecates one of these
 * classes, this output changes on its own and the page follows.
 *
 * Two genuinely deprecated members are checked alongside them as controls, so the result is a
 * comparison rather than a lone "false".
 */
public class E00_LegacyIsNotDeprecated {

    public static void main(String[] args) {
        Class<?>[] legacy = {
                Enumeration.class, BitSet.class, Stack.class,
                Vector.class, Dictionary.class, Hashtable.class
        };

        System.out.println("Are the six \"legacy\" classes actually deprecated?");
        System.out.println("java.lang.Deprecated is retained at run time, so this is a real test,");
        System.out.println("not a reading of the javadoc.");
        System.out.println();

        for (Class<?> c : legacy) {
            System.out.printf("  %-12s deprecated? %s%n",
                    c.getSimpleName(), c.isAnnotationPresent(Deprecated.class));
        }

        System.out.println();
        System.out.println("Members the JDK did deprecate, checked the same way:");
        report(Date.class, "getYear");
        report(Thread.class, "stop");

        System.out.println();
        System.out.println("A legacy method, checked the same way:");
        report(Vector.class, "addElement", Object.class);
    }

    /** Prints whether one method carries @Deprecated, or says so if this JDK no longer has it. */
    private static void report(Class<?> owner, String name, Class<?>... params) {
        String label = owner.getSimpleName() + "." + name + "()";
        try {
            Method m = owner.getMethod(name, params);
            System.out.printf("  %-18s deprecated? %s%n", label, m.isAnnotationPresent(Deprecated.class));
        } catch (NoSuchMethodException e) {
            System.out.printf("  %-18s (not present in this JDK)%n", label);
        }
    }
}
