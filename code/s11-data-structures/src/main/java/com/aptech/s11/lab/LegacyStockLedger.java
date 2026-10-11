package com.aptech.s11.lab;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;
import java.util.Vector;

/**
 * A stock ledger written the way it would have been written in 1996.
 *
 * Every choice here was the obvious one at the time: Vector for a growable list, Hashtable for
 * a lookup table, Stack for the pending orders, Enumeration to walk them, Properties for config.
 * All five compile without a single warning on JDK 21.
 *
 * It also contains three defects, and each one is caused by which class was chosen rather than
 * by a careless line of code. LabCheck shows all three; the lab is to fix them without changing
 * the behaviour the interface promises.
 */
public class LegacyStockLedger implements StockLedger {

    private final Vector<String> codes = new Vector<>();
    private final Hashtable<String, Integer> quantities = new Hashtable<>();
    private final Hashtable<String, Integer> prices = new Hashtable<>();
    private final Stack<String> pending = new Stack<>();
    private final Properties config = new Properties();

    public LegacyStockLedger() {
        try (InputStream in = LegacyStockLedger.class.getResourceAsStream("/app.properties")) {
            if (in != null) {
                config.load(in);
            }
        } catch (IOException ignored) {
            // 1996 code did not fuss; the default below keeps the program running.
        }
    }

    @Override
    public void record(String code, int quantity) {
        if (!codes.contains(code)) {
            codes.addElement(code);
        }
        quantities.put(code, quantity);
    }

    @Override
    public void setPrice(String code, Integer price) {
        // DEFECT 1. Hashtable refuses a null value, so "no price yet" cannot be stored at all.
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
        // DEFECT 2. search() is 1-based and measured from the top, so the newest order - which
        // has nothing after it - answers 1 instead of 0. The count is off by one, always.
        return pending.search(code);
    }

    @Override
    public int distinctCodes() {
        return codes.size();
    }

    @Override
    public int totalValue() {
        int total = 0;
        // DEFECT 3. Walking a Vector with an Enumeration while removing from it. An Enumeration
        // is not fail-fast, so this does not throw - it silently skips an entry and returns a
        // wrong number, which is worse.
        Enumeration<String> walk = codes.elements();
        while (walk.hasMoreElements()) {
            String code = walk.nextElement();
            Integer quantity = quantities.get(code);
            if (quantity == null || quantity == 0) {
                drop(code);
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

    private void drop(String code) {
        codes.removeElement(code);
        quantities.remove(code);
        prices.remove(code);
    }
}
