package com.aptech.s11.lab;

/**
 * The behaviour the lab has to keep.
 *
 * This interface is the whole point of the exercise: it describes WHAT a stock ledger does
 * without saying which classes do it. The legacy ledger behind it uses Vector, Hashtable,
 * Stack and Enumeration; the modern one uses ArrayList, HashMap and ArrayDeque. If both pass
 * the same checks, the rewrite changed nothing that anybody can observe.
 */
public interface StockLedger {

    /** Adds quantity to a code, creating it if needed. */
    void record(String code, int quantity);

    /** Sets the unit price. A null price means "not known yet" and must be accepted. */
    void setPrice(String code, Integer price);

    /** The unit price, or null when it has not been set. */
    Integer price(String code);

    /** Puts an order on the pending stack. The most recent order is on top. */
    void pushOrder(String code);

    /** How many pending orders were placed AFTER this one, or -1 when it is not pending. */
    int ordersAfter(String code);

    /** How many distinct codes the ledger knows about. */
    int distinctCodes();

    /** Total value of everything held: sum of quantity x price, ignoring codes with no price. */
    int totalValue();

    /** The currency symbol, read from configuration. */
    String currency();
}
