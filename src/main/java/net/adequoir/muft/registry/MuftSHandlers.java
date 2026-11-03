package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.registry.api.Registrar;
import net.adequoir.muft.registry.api.Registrars;
import net.adequoir.muft.screen.DiscPressScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

import java.util.function.Supplier;

public sealed interface MuftSHandlers permits RegistryLoader {
    Registrar<ScreenHandlerType<?>> REGISTRAR = Registrars.create(MusicalFeature.MOD_ID, Registries.SCREEN_HANDLER);

    Supplier<ScreenHandlerType<DiscPressScreenHandler>> DISC_PRESS = REGISTRAR.register(
        "disc_press",
        MuftSHandlers::create,
        factory(DiscPressScreenHandler::new)
    );

    static void init() {}

    private static <T extends ScreenHandler> ScreenHandlerType.Factory<T> factory(ScreenHandlerType.Factory<T> factory) {
        return factory;
    }

    private static <T extends ScreenHandler> ScreenHandlerType<T> create(ScreenHandlerType.Factory<T> factory) {
        return new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES);
    }
}
