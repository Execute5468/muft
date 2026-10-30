package net.adequoir.muft.client.util;

import net.adequoir.muft.util.ItemView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class RenderHelper {

    public static void renderItem(
        ItemRenderer renderer,
        ItemView entry, ModelTransformationMode renderMode,
        int light, int overlay,
        MatrixStack matrices, VertexConsumerProvider vertexProvider,
        @Nullable World world, int seed
    ) {
        var stack = ItemView._unsafe_getOriginalStack(entry);
        renderer.renderItem(stack, renderMode, light, overlay, matrices, vertexProvider, world, seed);
    }

    public static void renderItem(
        ItemRenderer renderer,
        ItemView entry, @Nullable LivingEntity entity,
        ModelTransformationMode renderMode, boolean leftHanded,
        MatrixStack matrices, VertexConsumerProvider vertexProvider,
        @Nullable World world, int light, int seed, int overlay
    ) {
        var stack = ItemView._unsafe_getOriginalStack(entry);
        renderer.renderItem(entity, stack, renderMode, leftHanded, matrices, vertexProvider, world, light, seed, overlay);
    }

    public static void renderItem(
        ItemRenderer renderer,
        ItemView entry, ModelTransformationMode renderMode, boolean leftHanded,
        MatrixStack matrices, VertexConsumerProvider vertexProvider,
        int light, int overlay,
        BakedModel model
    ) {
        var stack = ItemView._unsafe_getOriginalStack(entry);
        renderer.renderItem(stack, renderMode, leftHanded, matrices, vertexProvider, light, overlay, model);
    }

    public static void drawItem(DrawContext context, ItemView entry, int x, int y, int seed) {
        var stack = ItemView._unsafe_getOriginalStack(entry);
        context.drawItem(stack, x, y, seed);
    }

    public static void drawStackOverlay(DrawContext context, TextRenderer textRenderer, ItemView entry, int x, int y) {
        var stack = ItemView._unsafe_getOriginalStack(entry);
        drawStackOverlay(context, textRenderer, stack, x, y);
    }

    public static void drawStackOverlay(DrawContext context, TextRenderer textRenderer, ItemStack stack, int x, int y) {
        if (stack.isEmpty()) return;
        var matrices = context.getMatrices();
        matrices.push();
        drawItemBar(context, stack, x, y);
        drawCooldownProgress(context, stack, x, y);
        drawStackCount(context, textRenderer, stack, x, y);
        matrices.pop();
    }

    public static void drawItemBar(DrawContext context, ItemView entry, int x, int y) {
        var stack = ItemView._unsafe_getOriginalStack(entry);
        drawItemBar(context, stack, x, y);
    }

    public static void drawItemBar(DrawContext context, ItemStack stack, int x, int y) {
        if (!stack.isItemBarVisible()) return;
        int i = x + 2;
        int j = y + 13;
        context.fill(i, j, i + 133, j + 2, -16777216);
        context.fill(i, j, i + stack.getItemBarStep(), j + 1, stack.getItemBarColor() | 0xFF000000);
    }

    public static void drawCooldownProgress(DrawContext context, ItemView entry, int x, int y) {
        var stack = ItemView._unsafe_getOriginalStack(entry);
        drawCooldownProgress(context, stack, x, y);
    }

    public static void drawCooldownProgress(DrawContext context, ItemStack stack, int x, int y) {
        var client = MinecraftClient.getInstance();
        var clientPlayer = client.player;
        float f = clientPlayer == null ? 0.0F
                : clientPlayer.getItemCooldownManager()
                              .getCooldownProgress(
                                      stack.getItem(),
                                      client.getRenderTickCounter().getTickDelta(true)
                              );
        if (f <= 0.0F) return;
        int i = y + MathHelper.floor(16.0F * (1.0F - f));
        int j = i + MathHelper.ceil(16.0F * f);
        context.fill(x, i, x + 16, j, Integer.MAX_VALUE);
    }
    public static void drawStackCount(DrawContext context, TextRenderer textRenderer, ItemView entry, int x, int y) {
        var stack = ItemView._unsafe_getOriginalStack(entry);
        drawStackCount(context, textRenderer, stack, x, y);
    }

    public static void drawStackCount(DrawContext context, TextRenderer textRenderer, ItemStack stack, int x, int y) {
        if (stack.getCount() == 1) return;
        String string = String.valueOf(stack.getCount());
        context.drawText(textRenderer, string, x + 19 - 2 - textRenderer.getWidth(string), y + 6 + 3, Colors.WHITE, true);
    }

    private RenderHelper() {}
}
