package net.adequoir.muft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class DiscPressBlock extends HorizontalFacingBlock {
    public static final MapCodec<DiscPressBlock> CODEC = createCodec(DiscPressBlock::new);
    private static final Text TITLE = Text.translatable("container.muft.disc_press");

    public DiscPressBlock(Settings settings) { super(settings); }

    protected @Override MapCodec<? extends HorizontalFacingBlock> getCodec() { return CODEC; }

    public @Override @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    protected @Override void appendProperties(StateManager.Builder<Block, BlockState> builder) { builder.add(FACING); }

    protected @Override boolean hasSidedTransparency(BlockState state) { return true; }
}
