package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.block.DiscPressBlock;
import net.adequoir.muft.item.ItemEntries;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

import java.util.function.Function;

public final class MuftBlocks {
    public static final Block DISC_PRESS = register(
    "disc_press",
        DiscPressBlock::new,
        AbstractBlock.Settings.create()
                              .strength(3.5F, 3.5F)
                              .nonOpaque()
                              .solidBlock(Blocks::never)
                              .suffocates(Blocks::never)
                              .blockVision(Blocks::never)
                              .sounds(BlockSoundGroup.COPPER)
                              .instrument(NoteBlockInstrument.BASEDRUM)
    );

    static {
        ItemEntries.addAfter(ItemGroups.FUNCTIONAL, Blocks.BLAST_FURNACE, DISC_PRESS);
    }

    private static Block register(String name, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        var block = factory.apply(settings);
        var registryKey = RegistryKey.of(RegistryKeys.BLOCK, MusicalFeature.id(name));
        Registry.register(Registries.BLOCK, registryKey, block);
        Items.register(block);
        return block;
    }

    public static void init() {}
    private MuftBlocks() {}
}
