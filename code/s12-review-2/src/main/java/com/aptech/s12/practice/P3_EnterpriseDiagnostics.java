package com.aptech.s12.practice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.BitSet;

/**
 * PRACTICE 3 (Challenge) — Enterprise Diagnostic Clinic.
 *
 * Three subtle enterprise defects that produce misleading metrics or runtime crashes.
 *
 *   Fault 1: BitSet size() vs cardinality() - size() returns 64 (memory bits), not set count!
 *   Fault 2: ResultSet.next() omitted before reading columns - fails on empty queries.
 *   Fault 3: Missing Strategy pattern fallback - produces null instead of default rate.
 */
public class P3_EnterpriseDiagnostics {

    private static final String URL = "jdbc:h2:mem:s12_p3;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) throws SQLException {
        setupDatabase();

        System.out.println("Practice 3 (Challenge) - Enterprise Diagnostics: Three Subtle Defects");
        System.out.println();

        // Fault 1: BitSet metrics
        fault1_BitSetMetrics();
        System.out.println();

        // Fault 2: ResultSet navigation
        fault2_ResultSetNavigation();
        System.out.println();

        // Fault 3: Strategy pattern fallback
        fault3_StrategyFallback();
    }

    /** Fault 1: BitSet size() vs cardinality(). */
    static void fault1_BitSetMetrics() {
        BitSet flags = new BitSet();
        flags.set(0); // READ
        flags.set(3); // ADMIN

        int brokenCount = flags.size();        // Allocated word bits (64), NOT true bits!
        int fixedCount = flags.cardinality();  // Actual number of bits set to true (2)

        System.out.println("FAULT 1 - BitSet size() vs cardinality():");
        System.out.println("  broken (flags.size())        : " + brokenCount + "  <- Returns 64-bit word size, not set bits");
        System.out.println("  fixed (flags.cardinality()) : " + fixedCount + "  PASS - Accurately counts true bits (2)");
    }

    /** Fault 2: ResultSet cursor navigation before column read. */
    static void fault2_ResultSetNavigation() throws SQLException {
        System.out.println("FAULT 2 - ResultSet cursor navigation on non-existent record:");
        String nonExistentName = "Ghost Student";

        // Fixed pattern: check if (rs.next()) before reading
        String found = null;
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement("SELECT name FROM students WHERE name = ?")) {
            ps.setString(1, nonExistentName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    found = rs.getString("name");
                }
            }
        }
        System.out.println("  search for non-existent record -> returned: " + found + "  PASS - Safely returned null without crash");
    }

    /** Fault 3: Strategy pattern unknown policy handling. */
    static void fault3_StrategyFallback() {
        System.out.println("FAULT 3 - Strategy pattern discount calculation:");
        String discountType = "UNKNOWN_CODE";

        // Fixed: Provide fallback strategy rather than returning null
        double basePrice = 1000.0;
        double finalPrice = calculatePrice(basePrice, discountType);

        System.out.printf("  base price: $%.2f, policy: %s -> final price: $%.2f%n",
                basePrice, discountType, finalPrice);
        System.out.println("  PASS - Unknown discount safely defaulted to standard full rate without null.");
    }

    static double calculatePrice(double basePrice, String code) {
        return switch (code) {
            case "EARLY_BIRD" -> basePrice * 0.85;
            case "SCHOLARSHIP" -> basePrice * 0.50;
            default -> basePrice; // Robust fallback: no discount
        };
    }

    private static void setupDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE students (id INT PRIMARY KEY, name VARCHAR(50))");
            stmt.execute("INSERT INTO students VALUES (1, 'Ada Lovelace')");
        }
    }
}
