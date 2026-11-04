package net.adequoir.muft.client.gui.tooltip;

import net.adequoir.muft.client.input.Scroller;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.item.PocketJukeboxItem;
import net.adequoir.muft.api.client.network.ClientNetworkHelper;
import net.adequoir.muft.network.PocketJukeboxItemSelectedPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PocketJukeboxTooltipSubmenu implements TooltipSubmenu {
    private final Minecraft client;
    private final Scroller scroller;

    public PocketJukeboxTooltipSubmenu(Minecraft client) {
        this.client = client;
        this.scroller = new Scroller();
    }

    public @Override boolean isApplicableTo(Slot slot) { return slot.getItem().getItem() instanceof PocketJukeboxItem; }

    public @Override boolean onScroll(double horizontal, double vertical, int slotId, ItemStack item) {
        var component = PocketJukeboxComponent.get(item);
        int i = component.size();
        if (i == 0) return false;
        var vec2i = scroller.update(horizontal, vertical);
        int j = vec2i.y == 0 ? -vec2i.x : vec2i.y;
        if (j != 0) {
            int k = component.selectedIndex();
            int l = Scroller.scrollCycling(j, k, i);
            if (k != l) sendPacket(item, slotId, l);
        }
        return true;
    }

    public @Override void reset(Slot slot) { reset(slot.getItem(), slot.index); }

    public @Override void onMouseClick(Slot slot, ClickType actionType) {
        if (actionType != ClickType.QUICK_MOVE & actionType != ClickType.SWAP) return;
        reset(slot.getItem(), slot.index);
    }

    private void sendPacket(ItemStack item, int slotId, int selectedIndex) {
        var component = PocketJukeboxComponent.get(item);
        if (selectedIndex >= component.size()) return;
        var newComponent = component.withSelected(selectedIndex);
        PocketJukeboxComponent.set(item, newComponent);
        var payload = new PocketJukeboxItemSelectedPayload(slotId, selectedIndex);
        ClientNetworkHelper.send(payload);
    }

    public void reset(ItemStack item, int slotId) { sendPacket(item, slotId, -1); }
}