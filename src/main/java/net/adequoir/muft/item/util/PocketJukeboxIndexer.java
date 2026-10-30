package net.adequoir.muft.item.util;

import java.util.concurrent.atomic.AtomicLong;

public final class PocketJukeboxIndexer {
    public static PocketJukeboxIndexer INSTANCE = new PocketJukeboxIndexer();

    private final AtomicLong playbackCounter = new AtomicLong();

    public long getId() {
        long id;
        do { id = playbackCounter.incrementAndGet(); } while (id == 0L);
        return id;
    }

    private PocketJukeboxIndexer() {}
}
