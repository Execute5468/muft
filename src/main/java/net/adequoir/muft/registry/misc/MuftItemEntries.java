package net.adequoir.muft.registry.misc;

import net.adequoir.muft.item.ItemEntries;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;

import static net.adequoir.muft.registry.MuftItems.*;
import static net.adequoir.muft.registry.MuftBlocks.*;

public final class MuftItemEntries {

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
        ItemEntries.addAfter(ItemGroups.FUNCTIONAL, Blocks.BLAST_FURNACE, DISC_PRESS);
    }

    public static void init() {}

    private MuftItemEntries() {}
}
