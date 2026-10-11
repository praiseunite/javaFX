package com.aptech.s11;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * E04 — the Dictionary classes.
 *
 * Manual bullet 4 of 4: "Explain the Dictionary classes" — again plural, and again it matters.
 * Dictionary is the abstract parent; Hashtable is its only direct child and the one you will
 * meet in old code; Properties is Hashtable's child and the one member of the whole family that
 * is still the right tool today.
 */
public class E04_Dictionary {

    public static void main(String[] args) throws IOException {
        theAbstractParent();
        noNullsAllowed();
        propertiesTheSurvivor();
        resourceBundleIsBuiltOnIt();
    }

    private static void theAbstractParent() {
        System.out.println("1. Dictionary is the abstract parent, and Hashtable is its only child");
        System.out.println("   Dictionary is abstract? "
                + Modifier.isAbstract(Dictionary.class.getModifiers()));
        System.out.println("   Its javadoc says it is obsolete and superseded by Map.");
        System.out.println();

        Dictionary<String, String> countries = new Hashtable<>();
        countries.put("NG", "Nigeria");
        countries.put("GH", "Ghana");
        countries.put("KE", "Kenya");
        System.out.println("   A Dictionary variable holding a Hashtable:");
        System.out.println("   countries.get(\"NG\") -> " + countries.get("NG"));
        System.out.println("   countries.size()    -> " + countries.size());

        List<String> codes = new ArrayList<>();
        for (Enumeration<String> keys = countries.keys(); keys.hasMoreElements(); ) {
            codes.add(keys.nextElement());
        }
        Collections.sort(codes);
        System.out.println("   countries.keys()    -> an Enumeration, not an Iterator: " + codes);
        System.out.println();
        System.out.println("   So bullet 1 and bullet 4 are the same design: the Dictionary family");
        System.out.println("   hands out Enumerations. See E01 for what Iterator added.");
        System.out.println();
    }

    private static void noNullsAllowed() {
        System.out.println("2. Hashtable refuses null - both values and keys");
        Hashtable<String, String> legacy = new Hashtable<>();
        legacy.put("NG", "Nigeria");
        try {
            legacy.put("XX", null);
        } catch (NullPointerException ex) {
            System.out.println("   hashtable.put(\"XX\", null)      -> " + ex);
        }
        try {
            legacy.put(null, "Nowhere");
        } catch (NullPointerException ex) {
            System.out.println("   hashtable.put(null, \"Nowhere\") -> " + ex);
        }

        Map<String, String> modern = new HashMap<>();
        modern.put("NG", "Nigeria");
        modern.put("XX", null);
        modern.put(null, "Nowhere");
        System.out.println("   hashMap.put(\"XX\", null)        -> accepted");
        System.out.println("   hashMap.put(null, \"Nowhere\")   -> accepted");
        System.out.println();
        System.out.println("   A HashMap allows ONE null key and any number of null values. A Hashtable");
        System.out.println("   allows neither, so \"no value yet\" is a state it cannot even represent.");
        System.out.println("   That is the first bug in the lab.");

        try {
            new ConcurrentHashMap<String, String>().put("XX", null);
        } catch (NullPointerException ex) {
            System.out.println();
            System.out.println("   ConcurrentHashMap (Session 5) also refuses nulls, and deliberately: with");
            System.out.println("   several threads, get() returning null could not tell \"not there\" from");
            System.out.println("   \"there, and null\".");
        }
        System.out.println();
    }

    private static void propertiesTheSurvivor() throws IOException {
        System.out.println("3. Properties - the member of this family that is still the right tool");
        Properties config = new Properties();
        try (InputStream in = E04_Dictionary.class.getResourceAsStream("/app.properties")) {
            if (in == null) {
                throw new IOException("app.properties was not on the classpath");
            }
            config.load(in);
        }

        System.out.println("   loaded from app.properties:");
        for (String key : new TreeSet<>(config.stringPropertyNames())) {
            System.out.println("      " + key + " = " + config.getProperty(key));
        }
        System.out.println();
        System.out.println("   getProperty(\"stock.currency\")          -> " + config.getProperty("stock.currency"));
        System.out.println("   getProperty(\"stock.lowThreshold\")      -> " + config.getProperty("stock.lowThreshold")
                + "   (a String, not a number)");
        System.out.println("   getProperty(\"missing.key\")             -> " + config.getProperty("missing.key")
                + "   (null when absent)");
        System.out.println("   getProperty(\"missing.key\", \"fallback\") -> " + config.getProperty("missing.key", "fallback"));
        System.out.println("   new Properties() instanceof Hashtable  -> " + (new Properties() instanceof Hashtable));
        System.out.println();
        System.out.println("   The last line explains the first: Properties IS a Hashtable, so it inherits");
        System.out.println("   the same refusal of nulls and adds getProperty/setProperty on top. It");
        System.out.println("   survived because configuration files never went away.");
        System.out.println();
    }

    private static void resourceBundleIsBuiltOnIt() {
        System.out.println("4. ResourceBundle is Properties plus a naming rule (Session 9's mechanism)");
        ResourceBundle english = ResourceBundle.getBundle("labels", Locale.ENGLISH);
        ResourceBundle french = ResourceBundle.getBundle("labels", Locale.FRENCH);

        System.out.println("   labels.properties     -> greeting=" + english.getString("greeting")
                + ", farewell=" + english.getString("farewell"));
        System.out.println("   labels_fr.properties  -> greeting=" + french.getString("greeting")
                + ", farewell=" + french.getString("farewell"));
        System.out.println();
        System.out.println("   report.title exists in the base bundle only. Ask the French one for it:");
        System.out.println("   french.getString(\"report.title\") -> " + french.getString("report.title"));
        System.out.println();
        System.out.println("   No MissingResourceException: a missing translation falls back UP to the");
        System.out.println("   base bundle. That fallback is why labels_fr.properties has two lines and");
        System.out.println("   labels.properties has three - and it is the same fallback Session 9");
        System.out.println("   relied on when a locale was only half translated.");
        System.out.println();
        System.out.println("   Underneath all of it sits a Properties object reading a .properties file.");
    }
}
