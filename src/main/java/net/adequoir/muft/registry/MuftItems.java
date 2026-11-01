package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.item.ItemEntries;
import net.adequoir.muft.item.PocketJukeboxItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

//@SuppressWarnings("unused")
public final class MuftItems {
    public static final Item COPPER_NUGGET = getOrRegister(
        Identifier.ofVanilla("copper_nugget"),
        Item::new,
        new Item.Settings()
    );

    public static final Item POCKET_JUKEBOX = registerPocketJukebox("pocket_jukebox");

    public static final Item WHITE_POCKET_JUKEBOX = registerPocketJukebox("white_pocket_jukebox");
    public static final Item LIGHT_GRAY_POCKET_JUKEBOX = registerPocketJukebox("light_gray_pocket_jukebox");
    public static final Item GRAY_POCKET_JUKEBOX = registerPocketJukebox("gray_pocket_jukebox");
    public static final Item BLACK_POCKET_JUKEBOX = registerPocketJukebox("black_pocket_jukebox");
    public static final Item BROWN_POCKET_JUKEBOX = registerPocketJukebox("brown_pocket_jukebox");
    public static final Item RED_POCKET_JUKEBOX = registerPocketJukebox("red_pocket_jukebox");
    public static final Item ORANGE_POCKET_JUKEBOX = registerPocketJukebox("orange_pocket_jukebox");
    public static final Item YELLOW_POCKET_JUKEBOX = registerPocketJukebox("yellow_pocket_jukebox");
    public static final Item LIME_POCKET_JUKEBOX = registerPocketJukebox("lime_pocket_jukebox");
    public static final Item GREEN_POCKET_JUKEBOX = registerPocketJukebox("green_pocket_jukebox");
    public static final Item CYAN_POCKET_JUKEBOX = registerPocketJukebox("cyan_pocket_jukebox");
    public static final Item LIGHT_BLUE_POCKET_JUKEBOX = registerPocketJukebox("light_blue_pocket_jukebox");
    public static final Item BLUE_POCKET_JUKEBOX = registerPocketJukebox("blue_pocket_jukebox");
    public static final Item PURPLE_POCKET_JUKEBOX = registerPocketJukebox("purple_pocket_jukebox");
    public static final Item MAGENTA_POCKET_JUKEBOX = registerPocketJukebox("magenta_pocket_jukebox");
    public static final Item PINK_POCKET_JUKEBOX = registerPocketJukebox("pink_pocket_jukebox");

    public static final Item BLANK_DISC = register("blank_disc");

    public static final Item TRACK_DISC = register("track_disc", unstackable());
    public static final Item WHITE_TRACK_DISC = register("white_track_disc", unstackable());
    public static final Item LIGHT_GRAY_TRACK_DISC = register("light_gray_track_disc", unstackable());
    public static final Item GRAY_TRACK_DISC = register("gray_track_disc", unstackable());
    public static final Item BLACK_TRACK_DISC = register("black_track_disc", unstackable());
    public static final Item BROWN_TRACK_DISC = register("brown_track_disc", unstackable());
    public static final Item RED_TRACK_DISC = register("red_track_disc", unstackable());
    public static final Item ORANGE_TRACK_DISC = register("orange_track_disc", unstackable());
    public static final Item YELLOW_TRACK_DISC = register("yellow_track_disc", unstackable());
    public static final Item LIME_TRACK_DISC = register("lime_track_disc", unstackable());
    public static final Item GREEN_TRACK_DISC = register("green_track_disc", unstackable());
    public static final Item CYAN_TRACK_DISC = register("cyan_track_disc", unstackable());
    public static final Item LIGHT_BLUE_TRACK_DISC = register("light_blue_track_disc", unstackable());
    public static final Item BLUE_TRACK_DISC = register("blue_track_disc", unstackable());
    public static final Item PURPLE_TRACK_DISC = register("purple_track_disc", unstackable());
    public static final Item MAGENTA_TRACK_DISC = register("magenta_track_disc", unstackable());
    public static final Item PINK_TRACK_DISC = register("pink_track_disc", unstackable());

    static {
        ItemEntries.addBefore(
            ItemGroups.TOOLS, Items.MUSIC_DISC_13,
            POCKET_JUKEBOX,
            WHITE_POCKET_JUKEBOX,
            LIGHT_GRAY_POCKET_JUKEBOX,
            GRAY_POCKET_JUKEBOX,
            BLACK_POCKET_JUKEBOX,
            BROWN_POCKET_JUKEBOX,
            RED_POCKET_JUKEBOX,
            ORANGE_POCKET_JUKEBOX,
            YELLOW_POCKET_JUKEBOX,
            LIME_POCKET_JUKEBOX,
            GREEN_POCKET_JUKEBOX,
            CYAN_POCKET_JUKEBOX,
            LIGHT_BLUE_POCKET_JUKEBOX,
            BLUE_POCKET_JUKEBOX,
            PURPLE_POCKET_JUKEBOX,
            MAGENTA_POCKET_JUKEBOX,
            PINK_POCKET_JUKEBOX
        );
        ItemEntries.addAfter(ItemGroups.TOOLS, Items.MUSIC_DISC_PIGSTEP, BLANK_DISC);
        ItemEntries.addAfter(ItemGroups.INGREDIENTS, Items.IRON_NUGGET, COPPER_NUGGET);
    }

    private static Item.Settings pocketJukebox() {
        return new Item.Settings().maxCount(1)
                                  .component(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS, PocketJukeboxComponent.DEFAULT);
    }
    private static Item.Settings unstackable() { return new Item.Settings().maxCount(1); }

    private static Item registerPocketJukebox(String name) { return register(name, PocketJukeboxItem::new, pocketJukebox()); }

    private static Item register(String name) { return register(name, new Item.Settings()); }
    private static Item register(String name, Item.Settings settings) { return register(name, Item::new, settings); }
    private static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
        var id = MusicalFeature.id(name);
        var item = factory.apply(settings);
        return Items.register(id, item);
    }

    private static Item getOrRegister(Identifier id, Function<Item.Settings, Item> factory, Item.Settings settings) {
        var registryKey = RegistryKey.of(RegistryKeys.ITEM, id);
        var existingItem = Registries.ITEM.get(registryKey);
        if (existingItem != null) {
            MusicalFeature.LOGGER.info("Existing item with id {} found - skipping registration", id);
            return existingItem;
        }

        var newItem = factory.apply(settings);
        return Registry.register(Registries.ITEM, registryKey, newItem);
    }

    public static void init() {}

    private MuftItems() {}
}
