package net.adequoir.muft.client.gui.tooltip;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

public interface MuftTooltipComponent extends TooltipComponent {


    default void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        TooltipComponent.super.drawItems(textRenderer, x, y, context);
    }
}
