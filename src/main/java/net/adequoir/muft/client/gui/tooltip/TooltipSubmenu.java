package net.adequoir.muft.client.gui.tooltip;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public interface TooltipSubmenu {
    boolean isApplicableTo(Slot slot);

    boolean onScroll(double horizontal, double vertical, int slotId, ItemStack item);

    void reset(Slot slot);

    void onMouseClick(Slot slot, SlotActionType actionType);
}
