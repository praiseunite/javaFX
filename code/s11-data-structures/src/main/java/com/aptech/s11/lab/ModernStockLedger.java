package com.aptech.s11.lab;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * The same ledger, same interface, built from the classes Session 1 taught.
 *
 * Compare it with LegacyStockLedger line by line. Every defect there is gone, and the three
 * fixes were all class names:
 *
 *   Vector        -> ArrayList   (and the iteration bug goes with it)
 *   Hashtable     -> HashMap     (which accepts a null value, so "no price yet" exists)
 *   Stack         -> ArrayDeque  (which cannot be indexed, so a stack stays a stack)
 *   Enumeration   -> Iterator    (which fails fast, so the silent skip becomes a crash)
 *
 * Properties stays. It is the one class from the legacy family that is still the right tool.
 */
public class ModernStockLedger implements StockLedger {

    private final List<String> codes = new ArrayList<>();
    private final Map<String, Integer> quantities = new HashMap<>();
    private final Map<String, Integer> prices = new HashMap<>();
    private final Deque<String> pending = new ArrayDeque<>();
    private final Properties config = new Properties();

    public ModernStockLedger() {
        try (InputStream in = ModernStockLedger.class.getResourceAsStream("/app.properties")) {
            if (in != null) {
                config.load(in);
            }
        } catch (IOException ignored) {
            // The default below keeps the program running; a real app would log this.
        }
    }

    @Override
    public void record(String code, int quantity) {
        if (!codes.contains(code)) {
            codes.add(code);
        }
        quantities.put(code, quantity);
    }

    @Override
    public void setPrice(String code, Integer price) {
        // No check needed: HashMap accepts a null value, so "not known yet" is representable.
        prices.put(code, price);
    }

    @Override
    public Integer price(String code) {
        return prices.get(code);
    }

    @Override
    public void pushOrder(String code) {
        pending.push(code);
    }

    @Override
    public int ordersAfter(String code) {
        // The deque iterates from the head, which push() made the newest order. Count the
        // orders we pass on the way to this one: that is how many were placed after it.
        int seen = 0;
        for (String next : pending) {
            if (next.equals(code)) {
                return seen;
            }
            seen++;
        }
        return -1;
    }

    @Override
    public int distinctCodes() {
        return codes.size();
    }

    @Override
    public int totalValue() {
        int total = 0;
        // Removing through the iterator is legal, which is exactly what the legacy version
        // needed and did not have. The list never shifts underneath the loop.
        for (Iterator<String> walk = codes.iterator(); walk.hasNext(); ) {
            String code = walk.next();
            Integer quantity = quantities.get(code);
            if (quantity == null || quantity == 0) {
                walk.remove();
                quantities.remove(code);
                prices.remove(code);
                continue;
            }
            Integer unitPrice = prices.get(code);
            if (unitPrice != null) {
                total += quantity * unitPrice;
            }
        }
        return total;
    }

    @Override
    public String currency() {
        return config.getProperty("stock.currency", "NGN");
    }
}
