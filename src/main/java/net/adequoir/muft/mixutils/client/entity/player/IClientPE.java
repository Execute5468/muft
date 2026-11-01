package net.adequoir.muft.mixutils.client.entity.player;

import net.adequoir.muft.client.sound.PocketJukeboxManager;

public interface IClientPE {
    default PocketJukeboxManager muft_pocketJukeboxTicker() { throw new AssertionError(); }
}
