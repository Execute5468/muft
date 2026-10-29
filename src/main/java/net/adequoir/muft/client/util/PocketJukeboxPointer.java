package net.adequoir.muft.client.util;

import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.mixin.client.entity.player.AxPlayerInventory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record PocketJukeboxPointer(
    Item item,
    PocketJukeboxComponent jukeboxContent,
    int discIndex,
    int invType,
    int invSlot
) {
    public static final int T_CURSOR_STACK = -2;

    public static @Nullable PocketJukeboxPointer fromStack(ClientPlayerEntity player, ItemStack stack, int discIndex) {
        var component = PocketJukeboxComponent.getOrNull(stack);
        if (component == null || !component.inBounds(discIndex)) return null;

        var cursorStack = player.currentScreenHandler.getCursorStack();
        if (stack == cursorStack) return new PocketJukeboxPointer(stack.getItem(), component, discIndex, T_CURSOR_STACK, 0);

        var combinedInv = ((AxPlayerInventory) player.getInventory()).axCombinedInventory();
        for (int i = 0; i < combinedInv.size(); i++) {
            var inv = combinedInv.get(i);
            for (int j = 0; j < inv.size(); j++) {
                if (inv.get(j) != stack) continue;
                return new PocketJukeboxPointer(stack.getItem(), component, discIndex, i, j);
            }
        }
        return null;
    }

    public boolean test(ItemStack stack) {
        if (!stack.isOf(item)) return false;
        PocketJukeboxComponent component = PocketJukeboxComponent.getOrNull(stack);
        if (component == jukeboxContent) return true;
        if (component == null) return false;
        var currentDisc = PocketJukeboxComponent._unsafe_get(jukeboxContent, discIndex);
        if (PocketJukeboxComponent._unsafe_get(component, discIndex) == currentDisc) return true;
        if (component.size() <= 1) return false;
        for (int i = 0; i < component.size(); i++) {
            if (PocketJukeboxComponent._unsafe_get(component, i) == currentDisc) return true;
        }
        return false;
    }

    public @Nullable PocketJukeboxPointer update(ClientPlayerEntity player) {
        if (invType != T_CURSOR_STACK) {
            var combinedInventory = ((AxPlayerInventory) player.getInventory()).axCombinedInventory();
            var pointedStack = combinedInventory.get(invType).get(invSlot);

            return updateStack(player, pointedStack, true);
        } else {
            var cursorStack = player.currentScreenHandler.getCursorStack();
            return updateStack(player, cursorStack, false);
        }
    }

    private @Nullable PocketJukeboxPointer updateStack(ClientPlayerEntity player, ItemStack stack, boolean testCursor) {
        var equality = testEquality(stack);
        if (equality != Equality.NONE) return fromEquality(equality, stack, invType, invSlot);

        if (testCursor) {
            var cursorStack = player.currentScreenHandler.getCursorStack();
            var cursorEquality = testEquality(cursorStack);

            if (cursorEquality != Equality.NONE) return fromEquality(cursorEquality, cursorStack, T_CURSOR_STACK, invSlot);
        }

        var combinedInventory = ((AxPlayerInventory) player.getInventory()).axCombinedInventory();
        for (int i = 0; i < combinedInventory.size();i++) {
            var inv = combinedInventory.get(i);
            for (int j = 0; j < inv.size(); j++) {
                var otherStack = inv.get(j);
                var equality2 = testEquality(otherStack);
                if (equality2 != Equality.NONE) return fromEquality(equality2, otherStack, i, j);
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
        if (equality == Equality.COMPONENT) {
            if (invType == this.invType && invSlot == this.invSlot) return this;
            return new PocketJukeboxPointer(item, jukeboxContent, discIndex, invType, invSlot);
        }
        if (equality instanceof Equality.Disc disc) {
            return new PocketJukeboxPointer(
                item, PocketJukeboxComponent.get(stack),
                disc.index, invType, invSlot
            );
        }
        throw new IllegalArgumentException("Unsupported equality " + equality);
    }


    private Equality testEquality(ItemStack other) {
        if (!other.isOf(item)) return Equality.NONE;

        var otherComponent = PocketJukeboxComponent.getOrNull(other);
        if (otherComponent == null) return Equality.NONE;
        if (otherComponent == jukeboxContent) return Equality.COMPONENT;

        var currentDisc = PocketJukeboxComponent._unsafe_get(jukeboxContent, discIndex);
        if (otherComponent.inBounds(discIndex) && PocketJukeboxComponent._unsafe_get(otherComponent, discIndex) == currentDisc) return Equality.disc(discIndex);

        for (int i = 0; i < otherComponent.size(); i++) {
            var otherDisc = PocketJukeboxComponent._unsafe_get(otherComponent, i);
            if (otherDisc == currentDisc) return Equality.disc(i);
        }

        return Equality.NONE;
    }

    @SuppressWarnings("StaticInitializerReferencesSubClass")
    private static class Equality {
        public static final Equality COMPONENT = new Named("COMPONENT");
        public static final Equality NONE = new Named("NONE");

        static Equality disc(int index) { return new Disc(index); }

        static class Named extends Equality {
            private final String name;
            Named(String name) { this.name = name; }
            public @Override String toString() { return name; }
        }

        static class Disc extends Equality {
            final int index;
            Disc(int index) { this.index = index; }

            public @Override String toString() { return "Disc(" + index + ")"; }
        }
    }
}
