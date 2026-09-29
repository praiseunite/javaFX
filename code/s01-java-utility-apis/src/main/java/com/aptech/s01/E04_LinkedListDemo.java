package com.aptech.s01;

import java.util.LinkedList;

/**
 * Example 4 — LinkedList: a chain of nodes. Cheap to add/remove at the ends.
 */
public class E04_LinkedListDemo {
    public static void main(String[] args) {
        LinkedList<String> playlist = new LinkedList<>();
        playlist.add("Song B");
        playlist.addFirst("Song A");       // jump to the front — no shifting needed
        playlist.addLast("Song C");
        System.out.println("Playlist: " + playlist);

        System.out.println("Now playing: " + playlist.getFirst());
        System.out.println("Last song:   " + playlist.getLast());

        playlist.removeFirst();            // finished Song A
        System.out.println("Up next:  " + playlist);

        // A LinkedList is still a List, so get(index) works — it just walks the chain
        System.out.println("Index 1:  " + playlist.get(1));
    }
}
