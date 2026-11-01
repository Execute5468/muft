package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class MuftJukeboxSongTags {
    public static final TagKey<JukeboxSong> DISC_PRESSABLE = of("disc_pressable");

    private static TagKey<JukeboxSong> of(String id) { return TagKey.of(RegistryKeys.JUKEBOX_SONG, MusicalFeature.id(id)); }

    public static void init() {}
    private MuftJukeboxSongTags() {}
}
