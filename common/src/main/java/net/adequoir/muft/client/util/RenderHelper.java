package net.adequoir.muft.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.adequoir.muft.util.ItemEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class RenderHelper {

    public static void renderItem(
            ItemRenderer renderer,
            ItemEntry entry, ItemDisplayContext renderMode,
            int light, int overlay,
            PoseStack matrices, MultiBufferSource vertexProvider,
            @Nullable Level world, int seed
    ) {
        var stack = ItemEntry._unsafe_getOriginalStack(entry);
        renderer.renderStatic(stack, renderMode, light, overlay, matrices, vertexProvider, world, seed);
    }

    public static void renderItem(
            ItemRenderer renderer,
            ItemEntry entry, @Nullable LivingEntity entity,
            ItemDisplayContext renderMode, boolean leftHanded,
            PoseStack matrices, MultiBufferSource vertexProvider,
            @Nullable Level world, int light, int seed, int overlay
    ) {
        var stack = ItemEntry._unsafe_getOriginalStack(entry);
        renderer.renderStatic(entity, stack, renderMode, leftHanded, matrices, vertexProvider, world, light, seed, overlay);
    }

    public static void renderItem(
            ItemRenderer renderer,
            ItemEntry entry, ItemDisplayContext renderMode, boolean leftHanded,
            PoseStack matrices, MultiBufferSource vertexProvider,
            int light, int overlay,
            BakedModel model
    ) {
        var stack = ItemEntry._unsafe_getOriginalStack(entry);
        renderer.render(stack, renderMode, leftHanded, matrices, vertexProvider, light, overlay, model);
    }

    public static void drawItem(GuiGraphics context, ItemEntry entry, int x, int y, int seed) {
        var stack = ItemEntry._unsafe_getOriginalStack(entry);
        context.renderItem(stack, x, y, seed);
    }

    public static void drawStackOverlay(GuiGraphics context, Font textRenderer, ItemEntry entry, int x, int y) {
        var stack = ItemEntry._unsafe_getOriginalStack(entry);
        drawStackOverlay(context, textRenderer, stack, x, y);
    }

    public static void drawStackOverlay(GuiGraphics context, Font textRenderer, ItemStack stack, int x, int y) {
        if (stack.isEmpty()) return;
        var matrices = context.pose();
        matrices.pushPose();
        drawItemBar(context, stack, x, y);
        drawCooldownProgress(context, stack, x, y);
        drawStackCount(context, textRenderer, stack, x, y);
        matrices.popPose();
    }

    public static void drawItemBar(GuiGraphics context, ItemEntry entry, int x, int y) {
        var stack = ItemEntry._unsafe_getOriginalStack(entry);
        drawItemBar(context, stack, x, y);
    }

    public static void drawItemBar(GuiGraphics context, ItemStack stack, int x, int y) {
        if (!stack.isBarVisible()) return;
        int i = x + 2;
        int j = y + 13;
        context.fill(i, j, i + 133, j + 2, -16777216);
        context.fill(i, j, i + stack.getBarWidth(), j + 1, stack.getBarColor() | 0xFF000000);
    }

    public static void drawCooldownProgress(GuiGraphics context, ItemEntry entry, int x, int y) {
        var stack = ItemEntry._unsafe_getOriginalStack(entry);
        drawCooldownProgress(context, stack, x, y);
    }

    public static void drawCooldownProgress(GuiGraphics context, ItemStack stack, int x, int y) {
        var client = Minecraft.getInstance();
        var clientPlayer = client.player;
        float f = clientPlayer == null ? 0.0F
                : clientPlayer.getCooldowns()
                              .getCooldownPercent(
                                      stack.getItem(),
                                      client.getTimer().getGameTimeDeltaPartialTick(true)
                              );
        if (f <= 0.0F) return;
        int i = y + Mth.floor(16.0F * (1.0F - f));
        int j = i + Mth.ceil(16.0F * f);
        context.fill(x, i, x + 16, j, Integer.MAX_VALUE);
    }
    public static void drawStackCount(GuiGraphics context, Font textRenderer, ItemEntry entry, int x, int y) {
        var stack = ItemEntry._unsafe_getOriginalStack(entry);
        drawStackCount(context, textRenderer, stack, x, y);
    }

    public static void drawStackCount(GuiGraphics context, Font textRenderer, ItemStack stack, int x, int y) {
        if (stack.getCount() == 1) return;
        String string = String.valueOf(stack.getCount());
        context.drawString(textRenderer, string, x + 19 - 2 - textRenderer.width(string), y + 6 + 3, CommonColors.WHITE, true);
    }

    private RenderHelper() {}
}
