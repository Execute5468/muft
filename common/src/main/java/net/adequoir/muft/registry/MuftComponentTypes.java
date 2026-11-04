package net.adequoir.muft.registry;

import net.adequoir.muft.Muft;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.adequoir.muft.api.registry.Registrar;
import net.adequoir.muft.api.registry.Registrars;
import java.util.function.Supplier;

public sealed interface MuftComponentTypes permits RegistryLoader {
    Registrar<DataComponentType<?>> REGISTRAR = Registrars.create(Muft.MOD_ID, BuiltInRegistries.DATA_COMPONENT_TYPE);

    Supplier<DataComponentType<PocketJukeboxComponent>> POCKET_JUKEBOX_CONTENTS = REGISTRAR.register(
        "pocket_jukebox_contents",
        DataComponentType.Builder::build,
        DataComponentType.<PocketJukeboxComponent>builder().persistent(PocketJukeboxComponent.CODEC)
                     .networkSynchronized(PocketJukeboxComponent.PACKET_CODEC).cacheEncoding()
    );

    static void init() {}
}
