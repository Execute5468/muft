package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.UnaryOperator;

public final class MuftComponentTypes {
    public static final ComponentType<PocketJukeboxComponent> POCKET_JUKEBOX_CONTENTS = register(
        "pocket_jukebox_contents",
            builder -> builder.codec(PocketJukeboxComponent.CODEC)
                                                            .packetCodec(PocketJukeboxComponent.PACKET_CODEC).cache()
    );



    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            MusicalFeature.id(id),
            builderOperator.apply(ComponentType.builder()).build()
        );
    }

    private MuftComponentTypes() {}
}
