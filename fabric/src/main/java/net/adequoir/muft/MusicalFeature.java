package net.adequoir.muft;

import net.adequoir.muft.registry.RegistryLoader;
import net.adequoir.muft.registry.misc.MuftItemEntries;
import net.adequoir.muft.registry.misc.MuftPacks;
import net.adequoir.muft.registry.misc.MuftPayloads;
import net.adequoir.muft.registry.tag.MuftItemTags;
import net.fabricmc.api.ModInitializer;

public class MusicalFeature implements ModInitializer {

    public @Override void onInitialize() {
        MuftBootstrapper.init();
        RegistryLoader.init();
        MuftItemEntries.init();
        MuftItemTags.init();
        MuftPayloads.init();
        MuftPacks.init();
    }
}
