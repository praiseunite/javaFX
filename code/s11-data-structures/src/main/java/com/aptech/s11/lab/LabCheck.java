package com.aptech.s11.lab;

import java.util.function.Supplier;

/**
 * One set of checks, run against both ledgers.
 *
 * This is the whole discipline of the lab. Nothing here knows whether it is talking to the
 * legacy ledger or the modern one - it only knows the interface. When the checks come back
 * green for both, the rewrite is finished, and that is the only evidence that counts.
 *
 * Run it before you change anything and read the failures carefully; each one names the legacy
 * class that caused it.
 */
public class LabCheck {

    private static int passed;
    private static int failed;

    /** A single check: returns true when the ledger behaved as promised. */
    private interface Check {
        boolean run();
    }

    public static void main(String[] args) {
        System.out.println("One suite, two implementations.");
        System.out.println("The checks below know only the StockLedger interface - not which");
        System.out.println("classes are behind it. Run them against both and compare.");
        System.out.println();

        System.out.println("=== ModernStockLedger  (ArrayList, HashMap, ArrayDeque) ===");
        suite(ModernStockLedger::new);

        System.out.println();
        System.out.println("=== LegacyStockLedger  (Vector, Hashtable, Stack, Enumeration) ===");
        suite(LegacyStockLedger::new);

        System.out.println();
        System.out.println(passed + " passed, " + failed + " failed.");
        System.out.println();
        if (failed == 0) {
            System.out.println("Both ledgers agree. The rewrite changed nothing observable.");
        } else {
            System.out.println("Every failure is a legacy class choice, not a careless line:");
            System.out.println("  price/unknown       - Hashtable refuses a null VALUE");
            System.out.println("  orders/after-newest - Stack.search() counts from 1, from the top");
            System.out.println("  orders/after-oldest - same off-by-one, one deeper");
            System.out.println("  total/skips-empty   - Enumeration is not fail-fast, so removing");
            System.out.println("                        during the walk silently skips an entry");
        }
    }

    private static void suite(Supplier<StockLedger> factory) {
        check(factory, "record/counts", () -> {
            StockLedger ledger = factory.get();
            ledger.record("BOLT", 10);
            ledger.record("NUT", 4);
            return ledger.distinctCodes() == 2;
        });

        check(factory, "price/round-trip", () -> {
            StockLedger ledger = factory.get();
            ledger.record("BOLT", 10);
            ledger.setPrice("BOLT", 250);
            return Integer.valueOf(250).equals(ledger.price("BOLT"));
        });

        check(factory, "price/unknown-is-null", () -> {
            StockLedger ledger = factory.get();
            ledger.record("NUT", 4);
            ledger.setPrice("NUT", null);
            return ledger.price("NUT") == null;
        });

        check(factory, "total/simple", () -> {
            StockLedger ledger = factory.get();
            ledger.record("BOLT", 10);
            ledger.setPrice("BOLT", 250);
            ledger.record("NUT", 4);
            ledger.setPrice("NUT", 50);
            return ledger.totalValue() == 2700;
        });

        check(factory, "total/ignores-unpriced", () -> {
            StockLedger ledger = factory.get();
            ledger.record("BOLT", 10);
            ledger.setPrice("BOLT", 250);
            ledger.record("NUT", 4);          // no price set at all
            return ledger.totalValue() == 2500;
        });

        check(factory, "orders/after-newest-is-0", () -> {
            StockLedger ledger = factory.get();
            ledger.pushOrder("A");
            ledger.pushOrder("B");
            return ledger.ordersAfter("B") == 0;
        });

        check(factory, "orders/after-oldest", () -> {
            StockLedger ledger = factory.get();
            ledger.pushOrder("A");
            ledger.pushOrder("B");
            ledger.pushOrder("C");
            return ledger.ordersAfter("A") == 2;
        });

        check(factory, "orders/unknown-is-minus-1", () -> {
            StockLedger ledger = factory.get();
            ledger.pushOrder("A");
            return ledger.ordersAfter("ZZ") == -1;
        });

        check(factory, "total/skips-empty-without-losing-others", () -> {
            StockLedger ledger = factory.get();
            ledger.record("BOLT", 10);
            ledger.setPrice("BOLT", 100);
            ledger.record("NUT", 0);          // an empty line, priced but holding nothing
            ledger.setPrice("NUT", 50);
            ledger.record("WASHER", 5);
            ledger.setPrice("WASHER", 10);
            ledger.record("SCREW", 2);
            ledger.setPrice("SCREW", 20);
            // 10x100 + 0x50 + 5x10 + 2x20 = 1090
            return ledger.totalValue() == 1090;
        });

        check(factory, "config/currency-from-properties", () -> {
            StockLedger ledger = factory.get();
            return "NGN".equals(ledger.currency());
        });
    }

    private static void check(Supplier<StockLedger> factory, String name, Check body) {
        try {
            if (body.run()) {
                passed++;
                System.out.printf("  PASS  %s%n", name);
            } else {
                failed++;
                System.out.printf("  FAIL  %s   (wrong answer)%n", name);
            }
        } catch (RuntimeException ex) {
            failed++;
            System.out.printf("  FAIL  %s   (threw %s)%n", name, ex);
        }
    }
}
