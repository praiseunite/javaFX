package com.aptech.s05;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The java.util.concurrent toolbox: thread-safe collections that do the locking
 * for you. These are the classes to reach for before writing synchronized
 * yourself - they were written and tested by the people who built the JVM.
 */
public class E08_ConcurrentCollections {

    public static void main(String[] args) throws Exception {
        System.out.println("1. ConcurrentHashMap - four chefs record orders at the same time");
        Map<String, Integer> orders = new ConcurrentHashMap<>();
        Thread[] chefs = new Thread[4];
        for (int i = 0; i < chefs.length; i++) {
            String name = "Chef " + (char) ('A' + i);
            chefs[i] = new Thread(() -> {
                for (int n = 0; n < 1_000; n++) {
                    orders.merge(name, 1, Integer::sum);
                }
            }, name);
            chefs[i].start();
        }
        for (Thread chef : chefs) {
            chef.join();
        }
        int total = orders.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("   " + orders);
        System.out.println("   total = " + total + "   (expected 4000 - not one order lost)");
        System.out.println();

        System.out.println("2. BlockingQueue - a real kitchen pass that holds at most 2 dishes");
        BlockingQueue<String> pass = new ArrayBlockingQueue<>(2);

        Thread baker = new Thread(() -> {
            try {
                for (String dish : List.of("Croissant", "Baguette", "Puff-puff")) {
                    System.out.println("   baker wants to put " + dish + " on the pass ...");
                    pass.put(dish);                 // blocks by itself when the pass is full
                    System.out.println("   baker put " + dish + " on the pass");
                }
                pass.put("CLOSED");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "baker");

        Thread waiter = new Thread(() -> {
            try {
                for (String dish = pass.take(); !dish.equals("CLOSED"); dish = pass.take()) {
                    System.out.println("   waiter served " + dish);
                    Thread.sleep(40);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "waiter");

        baker.start();
        waiter.start();
        baker.join();
        waiter.join();
        System.out.println("   put() and take() did all the waiting and waking for us - no wait(), no notify().");
        System.out.println();

        System.out.println("3. CopyOnWriteArrayList - a list read far more often than it is changed");
        List<String> guests = new CopyOnWriteArrayList<>(List.of("Ada", "Ben"));
        guests.add("Chi");
        System.out.println("   guests = " + guests);
        System.out.println();
        System.out.println("Same jobs as Parts 2 to 4, none of the danger. Reach for java.util.concurrent");
        System.out.println("first; write synchronized by hand only when no class already solves it.");
    }
}
