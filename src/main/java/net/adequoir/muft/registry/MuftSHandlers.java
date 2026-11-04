package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.api.registry.Registrar;
import net.adequoir.muft.api.registry.Registrars;
import net.adequoir.muft.screen.DiscPressScreenHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import java.util.function.Supplier;

public sealed interface MuftSHandlers permits RegistryLoader {
    Registrar<MenuType<?>> REGISTRAR = Registrars.create(MusicalFeature.MOD_ID, BuiltInRegistries.MENU);

    Supplier<MenuType<DiscPressScreenHandler>> DISC_PRESS = REGISTRAR.register(
        "disc_press",
        MuftSHandlers::create,
        factory(DiscPressScreenHandler::new)
    );

    static void init() {}

    private static <T extends AbstractContainerMenu> MenuType.MenuSupplier<T> factory(MenuType.MenuSupplier<T> factory) {
        return factory;
    }

    private static <T extends AbstractContainerMenu> MenuType<T> create(MenuType.MenuSupplier<T> factory) {
        return new MenuType<>(factory, FeatureFlags.VANILLA_SET);
    }
}
