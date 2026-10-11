package com.aptech.s11.practice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.TreeSet;

/**
 * P5 - Bullet 4 continued: Properties, the member of the Dictionary family still in daily use.
 *
 * Two things make Properties more than a Hashtable<String,String>: a chain of defaults, and
 * getProperty with a fallback that never throws and never returns null.
 */
public class P5_ConfigWithDefaults {

    public static void main(String[] args) throws IOException {
        Properties defaults = new Properties();
        defaults.setProperty("host", "localhost");
        defaults.setProperty("port", "5432");
        defaults.setProperty("ssl", "false");

        Properties site = new Properties(defaults);
        site.setProperty("port", "6543");

        System.out.println("1. A Properties can carry its own defaults");
        System.out.println("   site.getProperty(\"port\") -> " + site.getProperty("port") + "   (set on the site)");
        System.out.println("   site.getProperty(\"host\") -> " + site.getProperty("host") + "   (from defaults)");
        System.out.println("   site.getProperty(\"ssl\")  -> " + site.getProperty("ssl") + "   (from defaults)");
        System.out.println("   site.size()               -> " + site.size() + "   (defaults are not copied in)");
        System.out.println("   but they are searched when walking:");
        for (Object key : new TreeSet<>(site.stringPropertyNames())) {
            System.out.println("      " + key + " = " + site.getProperty((String) key));
        }
        System.out.println();

        System.out.println("2. Asking for something that is not there");
        System.out.println("   getProperty(\"user\")           -> " + site.getProperty("user"));
        System.out.println("   getProperty(\"user\", \"guest\") -> " + site.getProperty("user", "guest"));
        System.out.println();

        System.out.println("3. Loading the real file, then overriding one key in code");
        Properties config = new Properties();
        try (InputStream in = P5_ConfigWithDefaults.class.getResourceAsStream("/app.properties")) {
            if (in == null) {
                throw new IOException("app.properties was not on the classpath");
            }
            config.load(in);
        }
        System.out.println("   stock.lowThreshold from the file -> " + config.getProperty("stock.lowThreshold"));
        config.setProperty("stock.lowThreshold", "2");
        System.out.println("   after overriding in code         -> " + config.getProperty("stock.lowThreshold"));
        System.out.println("   (the file is untouched; this is the in-memory copy)");
    }
}
