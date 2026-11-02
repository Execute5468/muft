package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class MuftItemTags {

    public static final TagKey<Item> POCKET_JUKEBOXES = of("pocket_jukeboxes");
    public static final TagKey<Item> TRACK_DISCS = of("track_discs");

    private static TagKey<Item> of(String id) {
        return TagKey.of(RegistryKeys.ITEM, MusicalFeature.id(id));
    }

    public static void init() {}
    private MuftItemTags() {}
}
