package com.aptech.s05.practice;

/**
 * PRACTICE 3 (Medium) — producer and consumer with wait() and notifyAll().
 *
 * The shelf holds at most TWO boxes. The packer fills it, the courier empties it,
 * and the two take turns. Notice that wait() is always inside a while loop: when a
 * thread wakes up it must re-check the condition, because the world may have changed
 * while it slept.
 */
public class P3_ProducerConsumer {

    /** A shelf that holds at most 2 boxes. */
    static class Shelf {
        private final String[] boxes = new String[2];
        private int count = 0;

        synchronized void put(String box) throws InterruptedException {
            while (count == boxes.length) {              // shelf full: wait for the courier
                System.out.println("  Packer waits: the shelf is full.");
                wait();
            }
            boxes[count++] = box;
            System.out.println("  Packer put " + box + " on the shelf (now holding " + count + ")");
            notifyAll();
        }

        synchronized String take() throws InterruptedException {
            while (count == 0) {                          // shelf empty: wait for the packer
                System.out.println("  Courier waits: the shelf is empty.");
                wait();
            }
            String box = boxes[0];                        // first in, first out
            System.arraycopy(boxes, 1, boxes, 0, --count);
            System.out.println("  Courier took " + box + " (shelf now holding " + count + ")");
            notifyAll();
            return box;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Shelf shelf = new Shelf();

        Thread packer = new Thread(() -> {
            try {
                shelf.put("Box 1");
                shelf.put("Box 2");
                shelf.put("Box 3");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "packer");

        Thread courier = new Thread(() -> {
            try {
                shelf.take();
                shelf.take();
                shelf.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "courier");

        packer.start();
        courier.start();
        packer.join();
        courier.join();

        System.out.println();
        System.out.println("All three boxes moved. The shelf never held more than 2, and the");
        System.out.println("courier never tried to take a box that was not there - because each");
        System.out.println("one waited instead of guessing.");
    }
}
