package net.adequoir.muft.registry;

import net.adequoir.muft.Muft;
import net.adequoir.muft.api.registry.Registrar;
import net.adequoir.muft.api.registry.Registrars;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface MuftStats permits RegistryLoader {
    Registrar<ResourceLocation> REGISTRAR = Registrars.create(Muft.MOD_ID, BuiltInRegistries.CUSTOM_STAT);

    Supplier<ResourceLocation> INTERACT_WITH_DISC_PRESS = register("interacted_with_disc_press");

    private static Supplier<ResourceLocation> register(String name) { return register(name ,StatFormatter.DEFAULT); }
    private static Supplier<ResourceLocation> register(String name, StatFormatter formatter) {
        var id = Muft.id(name);
        return REGISTRAR.registerWith(name, Function.identity(), id, identifier -> Stats.CUSTOM.get(identifier, formatter));
    }

    static void init() {}
}
