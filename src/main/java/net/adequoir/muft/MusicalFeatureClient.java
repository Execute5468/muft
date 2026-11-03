package net.adequoir.muft;

import net.adequoir.muft.client.config.MuftConfigManager;
import net.adequoir.muft.client.registry.MuftScreens;
import net.fabricmc.api.ClientModInitializer;

public class MusicalFeatureClient implements ClientModInitializer  {

    public @Override void onInitializeClient() {
        MuftClientBootstrapper.init();
        MuftConfigManager.init();
        MuftScreens.init();
    }
}
