package net.adequoir.muft.client.config;

import net.adequoir.muft.api.client.config.HasTooltip;
import net.adequoir.muft.api.client.config.Option;
import net.adequoir.muft.api.client.config.OptionType;
import net.adequoir.muft.client.sound.PocketJukeboxManager;

import java.io.File;

public final class MuftConfig {
    static final File FILE = new File("config/musical_feature.json");

    @HasTooltip private boolean renderSelectedDisc;

    @HasTooltip private boolean betterJukeboxTooltip;

    @HasTooltip(valueBased = true)
    @Option.Enum(type = OptionType.CYCLIC)
    private PocketJukeboxManager.SongOrder songOrder = PocketJukeboxManager.SongOrder.RANDOM;

    public boolean renderSelectedDisc() { return this.renderSelectedDisc; }
    public boolean betterJukeboxTooltip() { return this.betterJukeboxTooltip; }
    public PocketJukeboxManager.SongOrder songOrder() { return this.songOrder; }
}
