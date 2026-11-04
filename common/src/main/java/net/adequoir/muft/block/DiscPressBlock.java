package net.adequoir.muft.block;

import com.mojang.serialization.MapCodec;
import net.adequoir.muft.screen.DiscPressScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class DiscPressBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<DiscPressBlock> CODEC = simpleCodec(DiscPressBlock::new);
    private static final Component TITLE = Component.translatable("container.muft.disc_press");

    public DiscPressBlock(Properties settings) { super(settings); }

    protected @Override MapCodec<? extends HorizontalDirectionalBlock> codec() { return CODEC; }


    protected @Override InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;

        player.openMenu(state.getMenuProvider(world, pos)); //TODO increment stat
        return InteractionResult.CONSUME;
    }

    protected @Override MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        return new SimpleMenuProvider((syncId, playerInventory, player) -> {
            //noinspection CodeBlock2Expr
            return new DiscPressScreenHandler(syncId, playerInventory, ContainerLevelAccess.create(world, pos));
        }, TITLE);
    }

    public @Override @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }
    protected @Override void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }

    protected @Override boolean useShapeForLightOcclusion(BlockState state) { return true; }
}
