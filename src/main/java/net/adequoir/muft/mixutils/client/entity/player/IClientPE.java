package net.adequoir.muft.mixutils.client.entity.player;

import net.adequoir.muft.client.sound.PocketJukeboxTicker;

public interface IClientPE {
    default PocketJukeboxTicker muft_pocketJukeboxTicker() { throw new AssertionError(); }
}
