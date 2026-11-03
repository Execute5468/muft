package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.registry.api.Registrar;
import net.adequoir.muft.registry.api.Registrars;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;

import java.util.function.Supplier;

public sealed interface MuftComponentTypes permits RegistryLoader {
    Registrar<ComponentType<?>> REGISTRAR = Registrars.create(MusicalFeature.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    Supplier<ComponentType<PocketJukeboxComponent>> POCKET_JUKEBOX_CONTENTS = REGISTRAR.register(
        "pocket_jukebox_contents",
        ComponentType.Builder::build,
        ComponentType.<PocketJukeboxComponent>builder().codec(PocketJukeboxComponent.CODEC)
                     .packetCodec(PocketJukeboxComponent.PACKET_CODEC).cache()
    );

    static void init() {}
}
