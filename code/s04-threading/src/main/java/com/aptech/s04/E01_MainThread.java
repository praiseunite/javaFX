package com.aptech.s04;

/**
 * Example 1 — Every Java program already runs in a thread: the "main" thread.
 */
public class E01_MainThread {
    public static void main(String[] args) {
        Thread me = Thread.currentThread();            // "which thread is running THIS line?"

        System.out.println("Name:     " + me.getName());
        System.out.println("Priority: " + me.getPriority() + "  (1 = lowest, 5 = normal, 10 = highest)");
        System.out.println("Daemon?   " + me.isDaemon());
        System.out.println("Alive?    " + me.isAlive());
        System.out.println("State:    " + me.getState());

        me.setName("boss");                            // you may rename a thread
        System.out.println("Renamed:  " + Thread.currentThread().getName());
    }
}
