package net.adequoir.muft;

import net.adequoir.muft.client.gui.screen.ConfigScreen;
import net.adequoir.muft.item.NeoForgeCreativeMenuEntries;
import net.adequoir.muft.network.NeoForgePayloadRegistrar;
import net.adequoir.muft.registry.NeoForgeRegistrar;
import net.adequoir.muft.resource.NeoForgePackHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Consumer;

@Mod(Muft.MOD_ID)
public class MusicalFeature {

    public MusicalFeature(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(NeoForgePayloadRegistrar::initialize);
        modEventBus.addListener(NeoForgeCreativeMenuEntries::initialize);
        modEventBus.addListener(NeoForgePackHelper::initialize);
        modEventBus.addListener((Consumer<RegisterEvent>) NeoForgeRegistrar::initialize);
        modEventBus.addListener(MusicalFeatureClient::initialize);
        Muft.init();
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (minecraft, parent) -> new ConfigScreen(parent));
    }
}
