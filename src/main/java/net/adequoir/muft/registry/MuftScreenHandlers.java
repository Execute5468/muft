package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.screen.DiscPressScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public final class MuftScreenHandlers {
    public static final ScreenHandlerType<DiscPressScreenHandler> DISC_PRESS = register("disc_press", DiscPressScreenHandler::new);

    private static <T extends ScreenHandler>ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, MusicalFeature.id(id), new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }

    public static void init() {}
    private MuftScreenHandlers() {}
}
