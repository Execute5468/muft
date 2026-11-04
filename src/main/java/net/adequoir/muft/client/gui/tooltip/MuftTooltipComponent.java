package net.adequoir.muft.client.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public interface MuftTooltipComponent extends ClientTooltipComponent {


    default void drawItems(Font textRenderer, int x, int y, int width, int height, GuiGraphics context) {
        ClientTooltipComponent.super.renderImage(textRenderer, x, y, context);
    }
}
