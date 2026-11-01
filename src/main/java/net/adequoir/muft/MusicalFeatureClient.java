package net.adequoir.muft;

import net.adequoir.muft.client.registry.MuftClientPayloads;
import net.adequoir.muft.client.registry.MuftScreens;
import net.adequoir.muft.registry.MuftScreenHandlers;
import net.fabricmc.api.ClientModInitializer;

public class MusicalFeatureClient implements ClientModInitializer  {

    public @Override void onInitializeClient() {
        MuftScreens.init();
        MuftClientPayloads.init();
    }
}
