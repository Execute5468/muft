package net.adequoir.muft.client.config;

import java.io.File;

public final class MuftConfig {
    static final File FILE = new File("config/musical_feature.json");

    private boolean renderSelectedDisc;
    private boolean betterJukeboxTooltip;

    public boolean renderSelectedDisc() { return this.renderSelectedDisc; }
    public boolean betterJukeboxTooltip() { return this.betterJukeboxTooltip; }
}
