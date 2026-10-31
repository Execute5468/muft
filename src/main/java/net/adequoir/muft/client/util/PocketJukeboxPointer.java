package net.adequoir.muft.client.util;

import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.mixin.client.entity.player.AxPlayerInventory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record PocketJukeboxPointer(
    Item item,
    PocketJukeboxComponent component,
    int cachedType,
    int cachedSlot
) {
    public static final int T_CURSOR_STACK = -2;

    public int id() { return component.playbackId(); }

    public boolean test(ItemStack stack) { return testEquality(stack) != Equality.NONE; }

    public @Nullable PocketJukeboxPointer update(ClientPlayerEntity player) {
        if (cachedType != T_CURSOR_STACK) {
            var combinedInventory = ((AxPlayerInventory) player.getInventory()).axCombinedInventory();
            var pointedStack = combinedInventory.get(cachedType).get(cachedSlot);

            return updateStack(player, pointedStack, true);
        } else {
            var cursorStack = player.currentScreenHandler.getCursorStack();
            return updateStack(player, cursorStack, false);
        }
    }

    private @Nullable PocketJukeboxPointer updateStack(ClientPlayerEntity player, ItemStack stack, boolean testCursor) {
        var equality = testEquality(stack);
        if (equality != Equality.NONE) return fromEquality(equality, stack, cachedType, cachedSlot);

        if (testCursor) {
            var cursorStack = player.currentScreenHandler.getCursorStack();
            var cursorEquality = testEquality(cursorStack);
            if (cursorEquality != Equality.NONE) return fromEquality(cursorEquality, cursorStack, T_CURSOR_STACK, -1);
        }

        var combinedInv = ((AxPlayerInventory) player.getInventory()).axCombinedInventory();
        for (int i = 0; i < combinedInv.size(); i++) {
            var inv = combinedInv.get(i);
            for (int j = 0; j < inv.size(); j++) {
                var otherStack = inv.get(j);
                var otherEquality = testEquality(otherStack);
                if (otherEquality != Equality.NONE) return fromEquality(otherEquality, otherStack, i, j);
            }
        }
        return null;
    }

    private PocketJukeboxPointer fromEquality(
        Equality equality,
        ItemStack stack,
        int invType,
        int invSlot
    ) {
        if (equality == Equality.COMPONENT) return invType == cachedType && invSlot == cachedSlot
                ? this
                : new PocketJukeboxPointer(item, component, invType, invSlot);
        if (equality == Equality.ID) return new PocketJukeboxPointer(item, PocketJukeboxComponent.get(stack), invType, invSlot);
        throw new IllegalArgumentException("Unsupported equality " + equality);
    }

    private Equality testEquality(ItemStack other) {
        if (other.isEmpty()) return Equality.NONE;
        if (!other.isOf(item)) return Equality.NONE;

        var component = PocketJukeboxComponent.getOrNull(other);
        if (component == null) return Equality.NONE;
        if (component == this.component) return Equality.COMPONENT;
        if (component.playbackId() == this.component.playbackId()) return Equality.ID;
        return Equality.NONE;
    }

    private enum Equality { COMPONENT, ID, NONE }
}
