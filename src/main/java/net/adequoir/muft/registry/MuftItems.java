package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.item.ItemEntries;
import net.adequoir.muft.item.PocketJukeboxItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;

import java.util.function.Function;

@SuppressWarnings("unused")
public final class MuftItems {
    public static final Item POCKET_JUKEBOX = register(
        "pocket_jukebox",
        PocketJukeboxItem::new,
        new Item.Settings().maxCount(1).component(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS, PocketJukeboxComponent.DEFAULT)
    );

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
        ItemEntries.addBefore(ItemGroups.TOOLS, Items.MUSIC_DISC_13, POCKET_JUKEBOX);
        ItemEntries.addAfter(ItemGroups.TOOLS, Items.MUSIC_DISC_PIGSTEP, BLANK_DISC);
    }

    private static Item.Settings unstackable() { return new Item.Settings().maxCount(1); }

    private static Item register(String name) { return register(name, new Item.Settings()); }
    private static Item register(String name, Item.Settings settings) { return register(name, Item::new, settings); }
    private static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
        var id = MusicalFeature.id(name);
        var item = factory.apply(settings);
        return Items.register(id, item);
    }

    public static void init() {}

    private MuftItems() {}
}
