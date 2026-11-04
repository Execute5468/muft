package net.adequoir.muft.client.gui.tooltip;

import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface TooltipSubmenu {
    boolean isApplicableTo(Slot slot);

    boolean onScroll(double horizontal, double vertical, int slotId, ItemStack item);

    void reset(Slot slot);

    void onMouseClick(Slot slot, ClickType actionType);
}
