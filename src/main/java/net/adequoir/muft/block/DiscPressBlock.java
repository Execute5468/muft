package net.adequoir.muft.block;

import com.mojang.serialization.MapCodec;
import net.adequoir.muft.screen.DiscPressScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DiscPressBlock extends HorizontalFacingBlock {
    public static final MapCodec<DiscPressBlock> CODEC = createCodec(DiscPressBlock::new);
    private static final Text TITLE = Text.translatable("container.muft.disc_press");

    public DiscPressBlock(Settings settings) { super(settings); }

    protected @Override MapCodec<? extends HorizontalFacingBlock> getCodec() { return CODEC; }


    protected @Override ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        player.openHandledScreen(state.createScreenHandlerFactory(world, pos)); //TODO increment stat
        return ActionResult.CONSUME;
    }

    protected @Override NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, playerInventory, player) -> {
            //noinspection CodeBlock2Expr
            return new DiscPressScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos));
        }, TITLE);
    }

    public @Override @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    protected @Override void appendProperties(StateManager.Builder<Block, BlockState> builder) { builder.add(FACING); }

    protected @Override boolean hasSidedTransparency(BlockState state) { return true; }
}
