package net.adequoir.muft.registry.tag;

import net.adequoir.muft.Muft;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.JukeboxSong;

public final class MuftJukeboxSongTags {
    public static final TagKey<JukeboxSong> DISC_PRESSABLE = of("disc_pressable");

    private static TagKey<JukeboxSong> of(String id) { return TagKey.create(Registries.JUKEBOX_SONG, Muft.id(id)); }

    public static void init() {}
    private MuftJukeboxSongTags() {}
}
