package net.adequoir.muft.registry;

import net.adequoir.muft.Muft;
import net.adequoir.muft.api.registry.Registrar;
import net.adequoir.muft.api.registry.Registrars;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import java.util.function.Supplier;

public sealed interface MuftSoundEvents permits RegistryLoader {
    Registrar<SoundEvent> REGISTRAR = Registrars.create(Muft.MOD_ID, BuiltInRegistries.SOUND_EVENT);

    Supplier<SoundEvent> MUSIC_DISC_BEGINNING_2 = register("music_disc.beginning_2");
    Supplier<SoundEvent> MUSIC_DISC_EBB = register("music_disc.ebb");
    Supplier<SoundEvent> MUSIC_DISC_FLOATING_TREES = register("music_disc.floating_trees");
    Supplier<SoundEvent> MUSIC_DISC_HOME = register("music_disc.home");
    Supplier<SoundEvent> MUSIC_DISC_MEMORIES = register("music_disc.memories");
    Supplier<SoundEvent> MUSIC_DISC_MOOG_CITY_2 = register("music_disc.moog_city_2");
    Supplier<SoundEvent> MUSIC_DISC_MUTATION = register("music_disc.mutation");
    Supplier<SoundEvent> MUSIC_DISC_NIGHTLY = register("music_disc.nightly");
    Supplier<SoundEvent> MUSIC_DISC_SHORES = register("music_disc.shores");

    static void init() {}

    private static Supplier<SoundEvent> register(String path) {
        var id = ResourceLocation.fromNamespaceAndPath(Muft.MOD_ID, path);
        return REGISTRAR.register(path, SoundEvent.createVariableRangeEvent(id));
    }
}
