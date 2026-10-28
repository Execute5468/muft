package net.adequoir.muft.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;

public final class DrawHelper {

    public static void drawStackOverlay(DrawContext context, TextRenderer textRenderer, ItemStack stack, int x, int y) {
        if (stack.isEmpty()) return;
        var matrices = context.getMatrices();
        matrices.push();
        drawItemBar(context, stack, x, y);
        drawCooldownProgress(context, stack, x, y);
        drawStackCount(context, textRenderer, stack, x, y);
        matrices.pop();
    }

    public static void drawItemBar(DrawContext context, ItemStack stack, int x, int y) {
        if (!stack.isItemBarVisible()) return;
        int i = x + 2;
        int j = y + 13;
        context.fill(i, j, i + 133, j + 2, -16777216);
        context.fill(i, j, i + stack.getItemBarStep(), j + 1, stack.getItemBarColor() | 0xFF000000);
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

    public static void drawStackCount(DrawContext context, TextRenderer textRenderer, ItemStack stack, int x, int y) {
        if (stack.getCount() == 1) return;
        String string = String.valueOf(stack.getCount());
        context.drawText(textRenderer, string, x + 19 - 2 - textRenderer.getWidth(string), y + 6 + 3, Colors.WHITE, true);
    }

    private DrawHelper() {}
}
