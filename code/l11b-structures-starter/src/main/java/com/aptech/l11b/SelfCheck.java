package com.aptech.l11b;

/**
 * L11B SELF-CHECK — run this class to test your three structures. Aim for 11 passed, 0 failed.
 *
 * Each check names the requirement it is testing, so a FAIL tells you which piece of work is
 * still open. Fix one, run again, and watch the count climb - that is the whole method.
 */
public class SelfCheck {

    private static int passed = 0;
    private static int failed = 0;

    interface Test { void run(); }

    public static void main(String[] args) {
        System.out.println("=== L11B Self-Check ===\n");

        run("R1 add() appends and grows", () -> {
            IntList list = new IntList();
            for (int i = 1; i <= 20; i++) {
                list.add(i);
            }
            check(list.size() == 20, "size should be 20 but was " + list.size());
            check(list.capacity() >= 20, "the array must have grown to hold 20, capacity was " + list.capacity());
        });

        run("R2 get() returns element i, and refuses a bad index", () -> {
            IntList list = new IntList();
            list.add(10);
            list.add(20);
            list.add(30);
            check(list.get(0) == 10, "get(0) should be 10 but was " + list.get(0));
            check(list.get(2) == 30, "get(2) should be 30 but was " + list.get(2));
            check(threw(IndexOutOfBoundsException.class, () -> list.get(3)),
                    "get(3) on a list of three must throw IndexOutOfBoundsException");
            check(threw(IndexOutOfBoundsException.class, () -> list.get(-1)),
                    "get(-1) must throw IndexOutOfBoundsException");
        });

        run("R3 removeAt() removes and closes the gap", () -> {
            IntList list = new IntList();
            for (int i = 1; i <= 4; i++) {
                list.add(i);
            }
            check(list.removeAt(0) == 1, "removeAt(0) should return 1");
            check(list.size() == 3, "size after one removal should be 3 but was " + list.size());
            check(list.get(0) == 2 && list.get(2) == 4, "the elements after the gap must shift left");
            int last = list.removeAt(2);
            check(last == 4, "removeAt(2) should return 4 but returned " + last);
        });

        run("R4 grow() doubles the array and counts the copies", () -> {
            IntList list = new IntList();          // room for four
            for (int i = 0; i < 5; i++) {
                list.add(i);
            }
            check(list.capacity() == 8, "capacity should have doubled from 4 to 8, it was " + list.capacity());
            check(list.copies() == 4, "one growth of a full four-element array copies 4 elements, copies() said " + list.copies());
        });

        run("R5 put() stores and replaces; get() returns null when the key is absent", () -> {
            SimpleMap map = new SimpleMap();
            map.put("BOLT", 250);
            check(Integer.valueOf(250).equals(map.get("BOLT")), "get() should return the value just stored");
            map.put("BOLT", 999);
            check(Integer.valueOf(999).equals(map.get("BOLT")), "putting the same key twice must replace, not add");
            check(map.get("NUT") == null, "a key that was never stored must read as null");
        });

        run("R6 size() counts keys, not puts", () -> {
            SimpleMap map = new SimpleMap();
            map.put("a", 1);
            map.put("a", 2);
            check(map.size() == 1, "one key put twice is still one key, size said " + map.size());
            map.put("b", 3);
            check(map.size() == 2, "two keys should be size 2, size said " + map.size());
        });

        run("R7 the table doubles when it passes a 0.75 load factor", () -> {
            SimpleMap map = new SimpleMap();
            check(map.bucketCount() == 8, "a new map starts with 8 buckets, it had " + map.bucketCount());
            for (int i = 1; i <= 6; i++) {
                map.put(i, i);
            }
            check(map.bucketCount() == 8, "6 keys in 8 buckets is exactly three-quarters full - not past it - so no rehash yet (bucketCount " + map.bucketCount() + ")");
            map.put(7, 7);
            check(map.bucketCount() == 16, "the 7th key takes it past 0.75, so the table must double to 16 (bucketCount " + map.bucketCount() + ")");
            check(Integer.valueOf(1).equals(map.get(1)), "every key must still be reachable after a rehash");
        });

        run("R8 chainLengths() and longestChain() show a clustered spread", () -> {
            SimpleMap map = new SimpleMap();
            for (int i = 0; i < 6; i++) {
                map.put(i * 8, i);      // 0, 8, 16, 24, 32, 40 - every one a remainder of 0
            }
            check(map.chainLengths().length == map.bucketCount(), "chainLengths() needs one entry per bucket");
            check(map.longestChain() == 6, "six keys with the same remainder form one chain of 6, longestChain said " + map.longestChain());
            check(map.chainLengths()[0] == 6, "bucket 0 should hold all six, it held " + map.chainLengths()[0]);
        });

        run("R9 push+pop is a stack; push+poll is a queue", () -> {
            RingBuffer stack = new RingBuffer(4);
            stack.push("a");
            stack.push("b");
            stack.push("c");
            check("c".equals(stack.pop()), "pop() must give the NEWEST, which is c");
            check("b".equals(stack.pop()), "then b");
            RingBuffer queue = new RingBuffer(4);
            queue.push("a");
            queue.push("b");
            queue.push("c");
            check("a".equals(queue.poll()), "poll() must give the OLDEST, which is a");
            check("b".equals(queue.poll()), "then b");
        });

        run("R10 the indexes wrap, and the order survives it", () -> {
            RingBuffer ring = new RingBuffer(4);
            ring.push("gone");
            ring.poll();                                  // head and tail are both at slot 1 now
            ring.push("a");
            ring.push("b");
            ring.push("c");
            ring.push("d");                               // runs off the end of the array and back
            check(ring.head() == 1 && ring.tail() == 1,
                    "after a full turn both indexes are back at slot 1 (head " + ring.head() + ", tail " + ring.tail() + ")");
            check("[a, b, c, d]".equals(ring.toString()),
                    "the contents must still read oldest-first, they printed as " + ring);
            check("a".equals(ring.poll()), "after the wrap the oldest is still a");
            check("b".equals(ring.poll()), "and the next one out is b - the slot it sits in is irrelevant");
        });

        run("R11 an empty buffer refuses, and a full one refuses", () -> {
            RingBuffer empty = new RingBuffer(4);
            check(empty.isEmpty(), "a new buffer must be empty");
            check(!empty.isFull(), "and must not be full");
            check(threw(IllegalStateException.class, empty::poll), "poll() on an empty buffer must throw IllegalStateException");
            check(threw(IllegalStateException.class, empty::pop), "pop() on an empty buffer must throw IllegalStateException");
            RingBuffer full = new RingBuffer(2);
            full.push("a");
            full.push("b");
            check(full.isFull(), "two elements in two slots must be full");
            check(threw(IllegalStateException.class, () -> full.push("c")),
                    "pushing onto a full buffer must throw IllegalStateException, never overwrite");
        });

        System.out.println("\n" + passed + " passed, " + failed + " failed.");
        System.out.println(failed == 0
                ? "All three structures behave. Now run Main and read E01 to E06."
                : "Fix the FAILs above, then run SelfCheck again.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /** True when the action threw the exception we asked for - the type, not the wording. */
    private static boolean threw(Class<? extends Throwable> expected, Runnable action) {
        try {
            action.run();
            return false;
        } catch (Throwable thrown) {
            return expected.isInstance(thrown);
        }
    }

    private static void run(String name, Test test) {
        try {
            test.run();
            passed++;
            System.out.println("PASS  " + name);
        } catch (AssertionError e) {
            failed++;
            System.out.println("FAIL  " + name + "\n      -> " + e.getMessage());
        } catch (Exception e) {
            failed++;
            System.out.println("FAIL  " + name + "\n      -> crashed with " + e);
        }
    }
}
