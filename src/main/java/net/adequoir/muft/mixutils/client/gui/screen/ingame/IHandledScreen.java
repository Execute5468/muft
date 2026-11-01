package net.adequoir.muft.mixutils.client.gui.screen.ingame;

import net.adequoir.muft.client.gui.tooltip.TooltipSubmenu;

public interface IHandledScreen {
    void muft_addTooltipSubmenu(TooltipSubmenu submenu);

    boolean muft_mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
}
