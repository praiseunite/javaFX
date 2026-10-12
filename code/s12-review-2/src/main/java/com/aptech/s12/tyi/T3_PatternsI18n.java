package com.aptech.s12.tyi;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * TRY IT YOURSELF 3 — Book Session 8: Design Patterns &amp; Advanced Features.
 *
 * Task: Implement a Repository Factory pattern with an exhaustive switch expression,
 * and implement internationalization (i18n) message lookup with graceful English fallback.
 */
public class T3_PatternsI18n {

    public interface CourseRepository {
        void save(String code, String name);
        String find(String code);
        int count();
    }

    public static class InMemoryRepository implements CourseRepository {
        private final Map<String, String> map = new HashMap<>();
        @Override public void save(String code, String name) { map.put(code, name); }
        @Override public String find(String code) { return map.get(code); }
        @Override public int count() { return map.size(); }
    }

    public static class EncryptedRepository implements CourseRepository {
        private final Map<String, String> map = new HashMap<>();
        @Override public void save(String code, String name) { map.put(code, "[ENC]" + name); }
        @Override public String find(String code) { return map.get(code); }
        @Override public int count() { return map.size(); }
    }

    public enum StorageType { MEMORY, ENCRYPTED }

    public static class RepositoryFactory {
        public static CourseRepository create(StorageType type) {
            // Exhaustive switch expression: adding a constant will fail compilation if unhandled
            return switch (type) {
                case MEMORY -> new InMemoryRepository();
                case ENCRYPTED -> new EncryptedRepository();
            };
        }
    }

    public static class I18nHelper {
        private final ResourceBundle primaryBundle;
        private final ResourceBundle fallbackBundle;

        public I18nHelper(Locale locale) {
            this.primaryBundle = ResourceBundle.getBundle("Messages", locale, new FallbackControl());
            this.fallbackBundle = ResourceBundle.getBundle("Messages", Locale.ENGLISH, new FallbackControl());
        }

        public String get(String key) {
            try {
                return primaryBundle.getString(key);
            } catch (MissingResourceException e) {
                return fallbackBundle.getString(key);
            }
        }

        // Custom control to safely load bundle or simulate dictionary when standalone
        private static class FallbackControl extends ResourceBundle.Control {
            @Override
            public List<Locale> getCandidateLocales(String baseName, Locale locale) {
                return List.of(locale, Locale.ENGLISH);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Try It Yourself 3 - Repository Factory & I18N Fallback");

        // 1. Repository Factory Demonstration
        CourseRepository memRepo = RepositoryFactory.create(StorageType.MEMORY);
        memRepo.save("CS-101", "Computer Science I");

        CourseRepository encRepo = RepositoryFactory.create(StorageType.ENCRYPTED);
        encRepo.save("CS-101", "Computer Science I");

        System.out.println();
        System.out.println("1. Repository Factory implementations created through clean seam:");
        System.out.println("  Memory repo value    : " + memRepo.find("CS-101"));
        System.out.println("  Encrypted repo value : " + encRepo.find("CS-101"));

        // 2. I18N dictionary demonstration
        System.out.println();
        System.out.println("2. Internationalization (i18n) key lookup with English fallback:");

        Map<String, String> enDict = Map.of(
                "app.title", "Course Enrollment System",
                "btn.save", "Save Record",
                "btn.cancel", "Cancel");

        Map<String, String> frDict = Map.of(
                "app.title", "Système d'inscription aux cours",
                "btn.save", "Enregistrer");
                // Note: btn.cancel is intentionally missing in French

        System.out.println("  Language: French (Locale.FRENCH)");
        for (String key : List.of("app.title", "btn.save", "btn.cancel")) {
            String val = frDict.get(key);
            if (val != null) {
                System.out.printf("    %-12s -> %s (from French)%n", key, val);
            } else {
                System.out.printf("    %-12s -> %s (fallback from English)%n", key, enDict.get(key));
            }
        }
        System.out.println("  -> Missing localized keys safely fall back to the base language.");
    }
}
