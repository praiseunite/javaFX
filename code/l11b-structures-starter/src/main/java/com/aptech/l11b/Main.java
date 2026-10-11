package com.aptech.l11b;

/**
 * L11B — a small demo that uses YOUR three structures. GIVEN complete.
 *
 * Run SelfCheck first. This class is what you run afterwards, to watch the three of them work
 * together - and to see the numbers the lesson put on the page come out of your own code.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== IntList: growing, and what it costs ===");
        IntList numbers = new IntList();
        for (int i = 1; i <= 20; i++) {
            numbers.add(i);
        }
        System.out.println("   size " + numbers.size()
                + ", capacity " + numbers.capacity()
                + ", copies " + numbers.copies());
        System.out.println("   get(0) " + numbers.get(0) + ", get(19) " + numbers.get(19));
        System.out.println("   removeAt(0) returned " + numbers.removeAt(0)
                + ", so get(0) is now " + numbers.get(0));

        System.out.println();
        System.out.println("=== SimpleMap: where the keys landed ===");
        SimpleMap spread = new SimpleMap();
        for (int key : new int[]{40, 25, 33, 19, 12, 4}) {
            spread.put(key, key * 10);
        }
        System.out.println("   " + spread.size() + " keys in " + spread.bucketCount()
                + " buckets, longest chain " + spread.longestChain());
        int[] lengths = spread.chainLengths();
        for (int bucket = 0; bucket < lengths.length; bucket++) {
            if (lengths[bucket] > 0) {
                System.out.println("      bucket " + bucket + " holds " + lengths[bucket]);
            }
        }
        System.out.println("   get(33) -> " + spread.get(33) + ", get(999) -> " + spread.get(999));

        System.out.println();
        System.out.println("=== RingBuffer: two structures in one array ===");
        RingBuffer queue = new RingBuffer(8);
        queue.push("first");
        queue.push("second");
        queue.push("third");
        System.out.println("   as a queue: push x3 -> " + queue + ", poll -> " + queue.poll());

        RingBuffer stack = new RingBuffer(8);
        stack.push("first");
        stack.push("second");
        stack.push("third");
        System.out.println("   as a stack: push x3 -> " + stack + ", pop  -> " + stack.pop());

        RingBuffer wrapped = new RingBuffer(4);
        wrapped.push("gone");
        wrapped.poll();
        wrapped.push("a");
        wrapped.push("b");
        wrapped.push("c");
        wrapped.push("d");
        System.out.println("   after a wrap: head " + wrapped.head() + ", tail " + wrapped.tail()
                + ", contents " + wrapped);
        System.out.println("   poll x4 -> " + wrapped.poll() + ", " + wrapped.poll() + ", "
                + wrapped.poll() + ", " + wrapped.poll() + "   (oldest first, whatever the slots said)");

        System.out.println();
        System.out.println("All three agree with the lesson. Now read E01 to E06 in the session's");
        System.out.println("code folder - the same structures, with every operation counted.");
    }
}
