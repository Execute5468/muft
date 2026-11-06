package net.adequoir.muft;

import net.adequoir.muft.item.NeoForgeCreativeMenuEntries;
import net.adequoir.muft.network.NeoForgePayloadRegistrar;
import net.adequoir.muft.registry.NeoForgeRegistrar;
import net.adequoir.muft.resource.NeoForgePackHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Consumer;

@Mod(Muft.MOD_ID)
public class MusicalFeature {
    static ModContainer container;

    public MusicalFeature(IEventBus modBus, ModContainer modContainer) {
        container = modContainer;
        modBus.addListener(NeoForgePayloadRegistrar::initialize);
        modBus.addListener(NeoForgeCreativeMenuEntries::initialize);
        modBus.addListener(NeoForgePackHelper::initialize);
        modBus.addListener((Consumer<RegisterEvent>) NeoForgeRegistrar::initialize);
        Muft.init();
    }
}
