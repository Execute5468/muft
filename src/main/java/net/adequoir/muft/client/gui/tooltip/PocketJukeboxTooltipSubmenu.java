package net.adequoir.muft.client.gui.tooltip;

import net.adequoir.muft.client.input.Scroller;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.item.PocketJukeboxItem;
import net.adequoir.muft.network.packet.PocketJukeboxItemSelectedCSP;
import net.adequoir.muft.registry.MuftItemTags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class PocketJukeboxTooltipSubmenu implements TooltipSubmenu {
    private final MinecraftClient client;
    private final Scroller scroller;

    public PocketJukeboxTooltipSubmenu(MinecraftClient client) {
        this.client = client;
        this.scroller = new Scroller();
    }

    public @Override boolean isApplicableTo(Slot slot) { return slot.getStack().getItem() instanceof PocketJukeboxItem; }

    public @Override boolean onScroll(double horizontal, double vertical, int slotId, ItemStack item) {
        var component = PocketJukeboxComponent.get(item);
        int i = component.size();
        if (i == 0) return false;
        var vec2i = scroller.update(horizontal, vertical);
        int j = vec2i.y == 0 ? -vec2i.x : vec2i.y;
        if (j == 0) {
            int k = component.selectedIndex();
            int l = Scroller.scrollCycling(j, k, i);
            if (k != l) sendPacket(item, slotId, l);
        }
        return true;
    }

    public @Override void reset(Slot slot) { reset(slot.getStack(), slot.id); }

    public @Override void onMouseClick(Slot slot, SlotActionType actionType) {
        if (actionType != SlotActionType.QUICK_MOVE & actionType != SlotActionType.SWAP) return;
        reset(slot.getStack(), slot.id);
    }

    private void sendPacket(ItemStack item, int slotId, int selectedIndex) {
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler == null || selectedIndex >= PocketJukeboxComponent.get(item).size()) return;
        PocketJukeboxComponent.select(item, selectedIndex);
        networkHandler.sendPacket(new PocketJukeboxItemSelectedCSP(slotId, selectedIndex));
    }

    public void reset(ItemStack item, int slotId) { sendPacket(item, slotId, -1); }
}