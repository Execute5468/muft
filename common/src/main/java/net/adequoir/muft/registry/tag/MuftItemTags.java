package net.adequoir.muft.registry.tag;

import net.adequoir.muft.Muft;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class MuftItemTags {

    public static final TagKey<Item> POCKET_JUKEBOXES = of("pocket_jukeboxes");
    public static final TagKey<Item> TRACK_DISCS = of("track_discs");

    private static TagKey<Item> of(String id) {
        return TagKey.create(Registries.ITEM, Muft.id(id));
    }

    public static void init() {}
    private MuftItemTags() {}
}
