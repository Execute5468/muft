package net.adequoir.muft;

import net.fabricmc.api.ModInitializer;

public class MusicalFeature implements ModInitializer {

    public @Override void onInitialize() {
        MuftBootstrapper.init();
        Muft.init();
    }
}
