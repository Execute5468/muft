package net.adequoir.muft.client.config;

import net.adequoir.muft.api.client.config.HasTooltip;

import java.io.File;

public final class MuftConfig {
    static final File FILE = new File("config/musical_feature.json");

    private boolean renderSelectedDisc;

    @HasTooltip
    private boolean betterJukeboxTooltip;

    public boolean renderSelectedDisc() { return this.renderSelectedDisc; }
    public boolean betterJukeboxTooltip() { return this.betterJukeboxTooltip; }
}
