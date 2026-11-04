package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.item.PocketJukeboxItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.adequoir.muft.api.registry.Registrar;
import net.adequoir.muft.api.registry.Registrars;
import java.util.function.Supplier;

public sealed interface MuftItems permits RegistryLoader {
    Registrar<Item> REGISTRAR = Registrars.create(MusicalFeature.MOD_ID, BuiltInRegistries.ITEM);

    Supplier<Item> COPPER_NUGGET = REGISTRAR.register(ResourceLocation.withDefaultNamespace("copper_nugget"), Item::new, new Item.Properties());

    Supplier<Item> POCKET_JUKEBOX = registerPocketJukebox("pocket_jukebox");
    Supplier<Item> WHITE_POCKET_JUKEBOX = registerPocketJukebox("white_pocket_jukebox");
    Supplier<Item> LIGHT_GRAY_POCKET_JUKEBOX = registerPocketJukebox("light_gray_pocket_jukebox");
    Supplier<Item> GRAY_POCKET_JUKEBOX = registerPocketJukebox("gray_pocket_jukebox");
    Supplier<Item> BLACK_POCKET_JUKEBOX = registerPocketJukebox("black_pocket_jukebox");
    Supplier<Item> BROWN_POCKET_JUKEBOX = registerPocketJukebox("brown_pocket_jukebox");
    Supplier<Item> RED_POCKET_JUKEBOX = registerPocketJukebox("red_pocket_jukebox");
    Supplier<Item> ORANGE_POCKET_JUKEBOX = registerPocketJukebox("orange_pocket_jukebox");
    Supplier<Item> YELLOW_POCKET_JUKEBOX = registerPocketJukebox("yellow_pocket_jukebox");
    Supplier<Item> LIME_POCKET_JUKEBOX = registerPocketJukebox("lime_pocket_jukebox");
    Supplier<Item> GREEN_POCKET_JUKEBOX = registerPocketJukebox("green_pocket_jukebox");
    Supplier<Item> CYAN_POCKET_JUKEBOX = registerPocketJukebox("cyan_pocket_jukebox");
    Supplier<Item> LIGHT_BLUE_POCKET_JUKEBOX = registerPocketJukebox("light_blue_pocket_jukebox");
    Supplier<Item> BLUE_POCKET_JUKEBOX = registerPocketJukebox("blue_pocket_jukebox");
    Supplier<Item> PURPLE_POCKET_JUKEBOX = registerPocketJukebox("purple_pocket_jukebox");
    Supplier<Item> MAGENTA_POCKET_JUKEBOX = registerPocketJukebox("magenta_pocket_jukebox");
    Supplier<Item> PINK_POCKET_JUKEBOX = registerPocketJukebox("pink_pocket_jukebox");

    Supplier<Item> BLANK_DISC = REGISTRAR.register("blank_disc", Item::new, new Item.Properties());

    Supplier<Item> TRACK_DISC = registerUnstackable("track_disc");
    Supplier<Item> WHITE_TRACK_DISC = registerUnstackable("white_track_disc");
    Supplier<Item> LIGHT_GRAY_TRACK_DISC = registerUnstackable("light_gray_track_disc");
    Supplier<Item> GRAY_TRACK_DISC = registerUnstackable("gray_track_disc");
    Supplier<Item> BLACK_TRACK_DISC = registerUnstackable("black_track_disc");
    Supplier<Item> BROWN_TRACK_DISC = registerUnstackable("brown_track_disc");
    Supplier<Item> RED_TRACK_DISC = registerUnstackable("red_track_disc");
    Supplier<Item> ORANGE_TRACK_DISC = registerUnstackable("orange_track_disc");
    Supplier<Item> YELLOW_TRACK_DISC = registerUnstackable("yellow_track_disc");
    Supplier<Item> LIME_TRACK_DISC = registerUnstackable("lime_track_disc");
    Supplier<Item> GREEN_TRACK_DISC = registerUnstackable("green_track_disc");
    Supplier<Item> CYAN_TRACK_DISC = registerUnstackable("cyan_track_disc");
    Supplier<Item> LIGHT_BLUE_TRACK_DISC = registerUnstackable("light_blue_track_disc");
    Supplier<Item> BLUE_TRACK_DISC = registerUnstackable("blue_track_disc");
    Supplier<Item> PURPLE_TRACK_DISC = registerUnstackable("purple_track_disc");
    Supplier<Item> MAGENTA_TRACK_DISC = registerUnstackable("magenta_track_disc");
    Supplier<Item> PINK_TRACK_DISC = registerUnstackable("pink_track_disc");

    static void init() {}

    private static Supplier<Item> registerPocketJukebox(String name) {
        return REGISTRAR.register(name, PocketJukeboxItem::new, pocketJukeboxSettings());
    }

    private static Supplier<Item> registerUnstackable(String name) {
        return REGISTRAR.register(name, Item::new, unstackableSettings());
    }

    private static Supplier<Item.Properties> pocketJukeboxSettings() {
        return () -> new Item.Properties().stacksTo(1)
                                        .component(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS.get(), PocketJukeboxComponent.DEFAULT);
    }
    private static Item.Properties unstackableSettings() { return new Item.Properties().stacksTo(1); }
}
