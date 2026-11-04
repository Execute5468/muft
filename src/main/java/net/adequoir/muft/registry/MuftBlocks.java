package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.block.DiscPressBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.adequoir.muft.api.registry.Registrar;
import net.adequoir.muft.api.registry.Registrars;
import java.util.function.Supplier;

public sealed interface MuftBlocks permits RegistryLoader {
    Registrar<Block> REGISTRAR = Registrars.create(MusicalFeature.MOD_ID, BuiltInRegistries.BLOCK);

    Supplier<Block> DISC_PRESS = REGISTRAR.registerWith(
        "disc_press",
        DiscPressBlock::new,
        BlockBehaviour.Properties.of()
                              .strength(3.5F, 3.5F)
                              .noOcclusion()
                              .isRedstoneConductor(Blocks::never)
                              .isSuffocating(Blocks::never)
                              .isViewBlocking(Blocks::never)
                              .sound(SoundType.COPPER)
                              .instrument(NoteBlockInstrument.BASEDRUM),
        Items::registerBlock
    );

    static void init() {}
}