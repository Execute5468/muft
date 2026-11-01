package net.adequoir.muft.item;

import net.adequoir.muft.client.sound.PocketJukeboxTicker;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.item.util.PocketJukeboxIndexer;
import net.adequoir.muft.mixin.client.entity.player.AxPlayerInventory;
import net.adequoir.muft.mixutils.client.entity.player.IClientPE;
import net.adequoir.muft.network.PocketJukeboxPlayPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PocketJukeboxItem extends Item {
    private static final List<PocketJukeboxItem> pocketJukeboxes = new ArrayList<>();
    public static final List<PocketJukeboxItem> POCKET_JUKEBOXES = Collections.unmodifiableList(pocketJukeboxes);

    public PocketJukeboxItem(Item.Settings settings) {
        super(settings);
        pocketJukeboxes.add(this);
    }

    public @Override void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient || !(entity instanceof PlayerEntity player)) return;
        var invType = findInvType(stack, slot, player);
        if (invType >= 0) ((IClientPE) player).muft_pocketJukeboxTicker().tickStack(stack, invType, slot);
    }

    private static int findInvType(ItemStack stack, int slotId, PlayerEntity player) {
        final var combinedInv = ((AxPlayerInventory) player.getInventory()).axCombinedInventory();
        for (int i = 0; i < combinedInv.size(); i++) {
            var inv = combinedInv.get(i);
            if (slotId >= inv.size()) continue;
            if (inv.get(slotId) == stack) return i;
        }
        return -1;
    }

    public @Override boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        var component = PocketJukeboxComponent.getOrNull(stack);
        if (component == null) return false;

        var itemStack = slot.getStack();

        if (clickType == ClickType.LEFT && !itemStack.isEmpty()) {
            if (!component.isFull() && PocketJukeboxComponent.canInsert(itemStack)) {
                playInsertSound(player);
                var newComponent = component.withAdded(slot, player);
                PocketJukeboxComponent.set(stack, newComponent);
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
                    PocketJukeboxComponent.set(stack, pair.getLeft());
                    onContentChanged(player);
                }
                if (player.getWorld().isClient) {
                    onRemove(player, stack, PocketJukeboxTicker.T_CURSOR_STACK, -1, pair.getLeft(), component.selectedIndex());
                }
            }
            return true;
        }

        return false;
    }

    @Environment(EnvType.CLIENT)
    private static void onRemove(
        PlayerEntity player,
        ItemStack stack, int invType, int invSlot,
        PocketJukeboxComponent component, int discIndex
    ) {
        PocketJukeboxTicker.get((ClientPlayerEntity) player).onRemove(stack, invType, invSlot, component, discIndex);
    }

    @Environment(EnvType.CLIENT)
    private static void onRemove(PlayerEntity player, ItemStack stack, Slot slot, PocketJukeboxComponent component, int discIndex) {
        if (!(slot.inventory instanceof PlayerInventory)) return;
        var combinedInv = ((AxPlayerInventory) player.getInventory()).axCombinedInventory();

        var invSlot = slot.getIndex();
        for (int i = 0; i < combinedInv.size(); i++) {
            var invSize = combinedInv.get(i).size();
            if (invSlot >= invSize) { invSlot -= invSize; continue; }
            onRemove(player, stack, i, invSlot, component, discIndex);
            return;
        }
    }



    public @Override boolean onClicked(
        ItemStack stack, ItemStack otherStack, Slot slot,
        ClickType clickType, PlayerEntity player,
        StackReference cursorStackReference
    ) {
        if (clickType == ClickType.LEFT && otherStack.isEmpty()) {
            var newComponent = PocketJukeboxComponent.get(stack).withSelected(-1);
            PocketJukeboxComponent.set(stack, newComponent);
            return false;
        }
        var component = PocketJukeboxComponent.getOrNull(stack);
        if (component == null) return false;

        if (clickType == ClickType.LEFT && !otherStack.isEmpty()) {
            if (slot.canTakePartial(player) && !component.isFull() && PocketJukeboxComponent.canInsert(otherStack)) {
                playInsertSound(player);
                PocketJukeboxComponent.set(stack, component.withAdded(otherStack.split(1)));
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
                    PocketJukeboxComponent.set(stack, pair.getLeft());
                    onContentChanged(player);
                }
                if (player.getWorld().isClient) {
                    onRemove(player, stack, slot, pair.getLeft(), component.selectedIndex());
                }
            }
            return true;
        }

        var newComponent = PocketJukeboxComponent.get(stack).withSelected(-1);
        PocketJukeboxComponent.set(stack, newComponent);
        return false;
    }

    public @Override TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
        var component = PocketJukeboxComponent.get(stack);
        if (component.isEmpty()) return TypedActionResult.fail(stack);

        if (!world.isClient) {
            var selectedSlot = hand == Hand.MAIN_HAND ? user.getInventory().selectedSlot : -1;
            var playbackId = PocketJukeboxIndexer.INSTANCE.getId();

            var newComponent = component.withPlaying(playbackId);
            PocketJukeboxComponent.set(stack, newComponent);

            ServerPlayNetworking.send((ServerPlayerEntity) user, new PocketJukeboxPlayPayload(selectedSlot, playbackId));
        }
        return TypedActionResult.success(stack);
    }

    public @Override boolean isItemBarVisible(ItemStack stack) { return !PocketJukeboxComponent.get(stack).isEmpty(); }
    public @Override int getItemBarStep(ItemStack stack) { return PocketJukeboxComponent.sizeItemBarStep(stack); }
    public @Override int getItemBarColor(ItemStack stack) { return PocketJukeboxComponent.colorItemBar(stack); }

    public @Override Optional<TooltipData> getTooltipData(ItemStack stack) {
        return stack.contains(DataComponentTypes.HIDE_TOOLTIP) || stack.contains(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP)
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