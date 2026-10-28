package net.adequoir.muft.item;

import net.adequoir.muft.component.PocketJukeboxComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.adequoir.muft.registry.MuftComponentTypes.POCKET_JUKEBOX_CONTENTS;

public class PocketJukeboxItem extends Item {
    private static final List<PocketJukeboxItem> pocketJukeboxes = new ArrayList<>();
    public static final List<PocketJukeboxItem> POCKET_JUKEBOXES = Collections.unmodifiableList(pocketJukeboxes);

    public PocketJukeboxItem(Item.Settings settings) {
        super(settings);
        pocketJukeboxes.add(this);
    }

    public @Override boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        var component = PocketJukeboxComponent.getOrNull(stack);
        if (component == null) return false;

        var itemStack = slot.getStack();

        if (clickType == ClickType.LEFT && !itemStack.isEmpty()) {
            if (!component.isFull() && PocketJukeboxComponent.canInsert(stack)) {
                playInsertSound(player);
                var newComponent = component.withAdded(slot, player);
                stack.set(POCKET_JUKEBOX_CONTENTS, newComponent);
            } else playInsertFailSound(player);
            return true;
        }

        if (clickType == ClickType.RIGHT && itemStack.isEmpty()) {
            if (!component.isEmpty()) {
                var pair = component.withRemoved();
                var outStack = pair.getRight();
                if (outStack != null) {
                    slot.insertStack(outStack);
                    playRemoveOneSound(player);
                    stack.set(POCKET_JUKEBOX_CONTENTS, pair.getLeft());
                    onContentChanged(player);
                }
            }
            return true;
        }

        return false;
    }

    public @Override boolean onClicked(
        ItemStack stack, ItemStack otherStack, Slot slot,
        ClickType clickType, PlayerEntity player,
        StackReference cursorStackReference
    ) {
        if (clickType == ClickType.LEFT && otherStack.isEmpty()) {
            PocketJukeboxComponent.select(stack, -1);
            return false;
        }
        var component = stack.get(POCKET_JUKEBOX_CONTENTS);
        if (component == null) return false;

        if (clickType == ClickType.LEFT && !otherStack.isEmpty()) {
            if (slot.canTakePartial(player) && PocketJukeboxComponent.canInsert(otherStack)) {
                playInsertSound(player);
                stack.set(POCKET_JUKEBOX_CONTENTS, component.withAdded(otherStack));
                onContentChanged(player);
            } else playInsertFailSound(player);
            return true;
        }

        if (clickType == ClickType.RIGHT && otherStack.isEmpty()) {
            if (slot.canTakePartial(player)) {
                var pair = component.withRemoved();
                var removedStack = pair.getRight();
                if (removedStack != null) {
                    playRemoveOneSound(player);
                    cursorStackReference.set(removedStack);
                    stack.set(POCKET_JUKEBOX_CONTENTS, pair.getLeft());
                    onContentChanged(player);
                }
            }
            return true;
        }

        PocketJukeboxComponent.select(stack, -1);
        return false;
    }

    public @Override boolean isItemBarVisible(ItemStack stack) { return !PocketJukeboxComponent.get(stack).isEmpty(); }
    public @Override int getItemBarStep(ItemStack stack) { return PocketJukeboxComponent.sizeItemBarStep(stack); }
    public @Override int getItemBarColor(ItemStack stack) { return PocketJukeboxComponent.colorItemBar(stack); }

    public @Override Optional<TooltipData> getTooltipData(ItemStack stack) {
        return !stack.contains(DataComponentTypes.HIDE_TOOLTIP) && !stack.contains(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP)
                ? Optional.empty()
                : Optional.ofNullable(PocketJukeboxComponent.getOrNull(stack));
    }

    private static void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertFailSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 1.0F, 1.0F);
    }

    private void onContentChanged(PlayerEntity user) {
        var screenHandler = user.currentScreenHandler;
        if (screenHandler != null) screenHandler.onContentChanged(user.getInventory());
    }
}