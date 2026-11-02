package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public final class MuftSoundEvents {
    public static final SoundEvent MUSIC_DISC_BEGINNING_2 = register("music_disc.beginning_2");
    public static final SoundEvent MUSIC_DISC_EBB = register("music_disc.ebb");
    public static final SoundEvent MUSIC_DISC_FLOATING_TREES = register("music_disc.floating_trees");
    public static final SoundEvent MUSIC_DISC_HOME = register("music_disc.home");
    public static final SoundEvent MUSIC_DISC_MEMORIES = register("music_disc.memories");
    public static final SoundEvent MUSIC_DISC_MOOG_CITY_2 = register("music_disc.moog_city_2");
    public static final SoundEvent MUSIC_DISC_MUTATION = register("music_disc.mutation");
    public static final SoundEvent MUSIC_DISC_NIGHTLY = register("music_disc.nightly");
    public static final SoundEvent MUSIC_DISC_SHORES = register("music_disc.shores");

    private static SoundEvent register(String name) {
        var id = MusicalFeature.id(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void init() {}
    private MuftSoundEvents() {}
}
