package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.block.DiscPressBlock;
import net.adequoir.muft.api.registry.Registrar;
import net.adequoir.muft.api.registry.Registrars;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.sound.BlockSoundGroup;

import java.util.function.Supplier;

public sealed interface MuftBlocks permits RegistryLoader {
    Registrar<Block> REGISTRAR = Registrars.create(MusicalFeature.MOD_ID, Registries.BLOCK);

    Supplier<Block> DISC_PRESS = REGISTRAR.registerWith(
        "disc_press",
        DiscPressBlock::new,
        AbstractBlock.Settings.create()
                              .strength(3.5F, 3.5F)
                              .nonOpaque()
                              .solidBlock(Blocks::never)
                              .suffocates(Blocks::never)
                              .blockVision(Blocks::never)
                              .sounds(BlockSoundGroup.COPPER)
                              .instrument(NoteBlockInstrument.BASEDRUM),
        Items::register
    );

    static void init() {}
}