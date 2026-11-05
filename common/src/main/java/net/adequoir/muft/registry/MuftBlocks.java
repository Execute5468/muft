package net.adequoir.muft.registry;

import net.adequoir.muft.Muft;
import net.adequoir.muft.block.DiscPressBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.adequoir.muft.api.registry.Registrar;
import net.adequoir.muft.api.registry.Registrars;
import java.util.function.Supplier;

public sealed interface MuftBlocks permits RegistryLoader {
    BlockBehaviour.StatePredicate NEVER = (a, b, c) -> false;
    Registrar<Block> REGISTRAR = Registrars.create(Muft.MOD_ID, BuiltInRegistries.BLOCK);

    Supplier<Block> DISC_PRESS = REGISTRAR.register(
        "disc_press",
        DiscPressBlock::new,
        BlockBehaviour.Properties.of()
                              .strength(3.5F, 3.5F)
                              .noOcclusion()
                              .isRedstoneConductor(NEVER)
                              .isSuffocating(NEVER)
                              .isViewBlocking(NEVER)
                              .sound(SoundType.COPPER)
                              .instrument(NoteBlockInstrument.BASEDRUM)
    );

    static void init() {}
}