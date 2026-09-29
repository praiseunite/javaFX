package com.aptech.s04.practice;

/** Practice 1 (easy) — create three named threads with lambdas; each prints its own details. */
public class P1_ThreadInfo {
    public static void main(String[] args) throws InterruptedException {
        String[] names = {"reader", "writer", "printer"};
        for (String name : names) {
            Thread t = new Thread(() -> {
                Thread me = Thread.currentThread();
                System.out.println(me.getName() + " | priority " + me.getPriority()
                        + " | daemon " + me.isDaemon() + " | state " + me.getState());
            }, name);
            t.start();
            t.join();                                  // one at a time, so the output is in order
            System.out.println("  after join, " + name + " is " + t.getState());
        }
    }
}
