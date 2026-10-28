package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.item.PocketJukeboxItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.function.Function;

public final class MuftItems {
    public static final Item POCKET_JUKEBOX = register(
        "pocket_jukebox",
        PocketJukeboxItem::new,
        new Item.Settings().maxCount(1).component(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS, PocketJukeboxComponent.DEFAULT)
    );

    private static Item register(String string, Function<Item.Settings, Item> factory, Item.Settings settings) {
        var id = MusicalFeature.id(string);
        var item = factory.apply(settings);
        return Items.register(id, item);
    }

    public static void init() {}

    private MuftItems() {}
}
