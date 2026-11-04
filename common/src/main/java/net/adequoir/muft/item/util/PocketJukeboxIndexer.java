package net.adequoir.muft.item.util;

import java.util.concurrent.atomic.AtomicInteger;

public final class PocketJukeboxIndexer {
    public static PocketJukeboxIndexer INSTANCE = new PocketJukeboxIndexer();

    private final AtomicInteger playbackCounter = new AtomicInteger();

    public int getId() {
        int id;
        do { id = playbackCounter.incrementAndGet(); } while (id == 0);
        return id;
    }

    private PocketJukeboxIndexer() {}
}
