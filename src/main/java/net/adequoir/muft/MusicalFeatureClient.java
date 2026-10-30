package net.adequoir.muft;

import net.adequoir.muft.client.registry.MuftClientPayloads;
import net.fabricmc.api.ClientModInitializer;

public class MusicalFeatureClient implements ClientModInitializer  {

    public @Override void onInitializeClient() {
        MuftClientPayloads.init();
    }
}
