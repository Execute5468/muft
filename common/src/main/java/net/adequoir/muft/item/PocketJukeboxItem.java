package net.adequoir.muft.item;

import net.adequoir.muft.client.sound.PocketJukeboxManager;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.item.util.PocketJukeboxIndexer;
import net.adequoir.muft.mixin.client.entity.player.AxPlayerInventory;
import net.adequoir.muft.mixutils.client.entity.player.IClientPE;
import net.adequoir.muft.network.PocketJukeboxPlayPayload;
import net.adequoir.muft.api.network.ServerNetworkHelper;
import net.adequoir.muft.registry.MuftItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PocketJukeboxItem extends Item {
    private static final List<PocketJukeboxItem> pocketJukeboxes = new ArrayList<>();
    public static final List<PocketJukeboxItem> POCKET_JUKEBOXES = Collections.unmodifiableList(pocketJukeboxes);

    public PocketJukeboxItem(Item.Properties settings) {
        super(settings);
        pocketJukeboxes.add(this);
    }

    public @Override void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (!world.isClientSide || !(entity instanceof Player player)) return;
        var invType = findInvType(stack, slot, player);
        if (invType >= 0) ((IClientPE) player).muft_pocketJukeboxTicker().tickStack(stack, invType, slot);
    }

    private static int findInvType(ItemStack stack, int slotId, Player player) {
        final var combinedInv = ((AxPlayerInventory) player.getInventory()).axCombinedInventory();
        for (int i = 0; i < combinedInv.size(); i++) {
            var inv = combinedInv.get(i);
            if (slotId >= inv.size()) continue;
            if (inv.get(slotId) == stack) return i;
        }
        return -1;
    }

    public @Override boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction clickType, Player player) {
        var component = PocketJukeboxComponent.getOrNull(stack);
        if (component == null) return false;

        var itemStack = slot.getItem();

        if (clickType == ClickAction.PRIMARY && !itemStack.isEmpty()) {
            if (!component.isFull() && PocketJukeboxComponent.canInsert(itemStack)) {
                playInsertSound(player);
                var newComponent = component.withAdded(slot, player);
                PocketJukeboxComponent.set(stack, newComponent);
            } else playInsertFailSound(player);
            return true;
        }

        if (clickType == ClickAction.SECONDARY && itemStack.isEmpty()) {
            if (!component.isEmpty()) {
                var pair = component.withRemoved();
                var outStack = pair.getB();
                if (outStack != null) {
                    slot.safeInsert(outStack);
                    playRemoveOneSound(player);
                    PocketJukeboxComponent.set(stack, pair.getA());
                    onContentChanged(player);
                }
                if (player.level().isClientSide) {
                    PocketJukeboxManager.onRemove(player, stack, PocketJukeboxManager.T_CURSOR_STACK, -1, pair.getA(), component.boundSelectedIndex());
                }
            }
            return true;
        }

        return false;
    }

    public @Override boolean overrideOtherStackedOnMe(
        ItemStack stack, ItemStack otherStack, Slot slot,
        ClickAction clickType, Player player,
        SlotAccess cursorStackReference
    ) {
        if (clickType == ClickAction.PRIMARY && otherStack.isEmpty()) {
            var newComponent = PocketJukeboxComponent.get(stack).withSelected(-1);
            PocketJukeboxComponent.set(stack, newComponent);
            return false;
        }
        var component = PocketJukeboxComponent.getOrNull(stack);
        if (component == null) return false;

        if (clickType == ClickAction.PRIMARY && !otherStack.isEmpty()) {
            if (slot.allowModification(player) && !component.isFull() && PocketJukeboxComponent.canInsert(otherStack)) {
                playInsertSound(player);
                PocketJukeboxComponent.set(stack, component.withAdded(otherStack.split(1)));
                onContentChanged(player);
            } else playInsertFailSound(player);
            return true;
        }

        if (clickType == ClickAction.SECONDARY && otherStack.isEmpty()) {
            if (slot.allowModification(player)) {
                var pair = component.withRemoved();
                var removedStack = pair.getB();
                if (removedStack != null) {
                    playRemoveOneSound(player);
                    cursorStackReference.set(removedStack);
                    PocketJukeboxComponent.set(stack, pair.getA());
                    onContentChanged(player);
                }
                if (player.level().isClientSide) {
                    PocketJukeboxManager.onRemove(player, stack, slot, pair.getA(), component.boundSelectedIndex());
                }
            }
            return true;
        }

        var newComponent = PocketJukeboxComponent.get(stack).withSelected(-1);
        PocketJukeboxComponent.set(stack, newComponent);
        return false;
    }

    public @Override InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        var stack = user.getItemInHand(hand);
        var component = PocketJukeboxComponent.get(stack);
        if (component.isEmpty()) return InteractionResultHolder.fail(stack);

        if (!world.isClientSide) {
            var selectedSlot = hand == InteractionHand.MAIN_HAND ? user.getInventory().selected : -1;
            var playbackId = PocketJukeboxIndexer.INSTANCE.getId();

            var newComponent = component.withPlaying(playbackId);
            PocketJukeboxComponent.set(stack, newComponent);

            ServerNetworkHelper.send((ServerPlayer) user, new PocketJukeboxPlayPayload(selectedSlot, playbackId));
        }
        return InteractionResultHolder.success(stack);
    }

    public @Override boolean isBarVisible(ItemStack stack) { return !PocketJukeboxComponent.get(stack).isEmpty(); }
    public @Override int getBarWidth(ItemStack stack) { return PocketJukeboxComponent.sizeItemBarStep(stack); }
    public @Override int getBarColor(ItemStack stack) { return PocketJukeboxComponent.colorItemBar(stack); }

    public @Override Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return stack.has(DataComponents.HIDE_TOOLTIP) || stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
                ? Optional.empty()
                : Optional.ofNullable(PocketJukeboxComponent.getOrNull(stack));
    }

    public static Item get(@Nullable DyeColor color) {
        return switch (color) {
            case WHITE -> MuftItems.WHITE_POCKET_JUKEBOX.get();
            case LIGHT_GRAY -> MuftItems.LIGHT_GRAY_POCKET_JUKEBOX.get();
            case GRAY -> MuftItems.GRAY_POCKET_JUKEBOX.get();
            case BLACK -> MuftItems.BLACK_POCKET_JUKEBOX.get();
            case BROWN -> MuftItems.BROWN_POCKET_JUKEBOX.get();
            case RED -> MuftItems.RED_POCKET_JUKEBOX.get();
            case ORANGE -> MuftItems.ORANGE_POCKET_JUKEBOX.get();
            case YELLOW -> MuftItems.YELLOW_POCKET_JUKEBOX.get();
            case LIME -> MuftItems.LIME_POCKET_JUKEBOX.get();
            case GREEN -> MuftItems.GREEN_POCKET_JUKEBOX.get();
            case CYAN -> MuftItems.CYAN_POCKET_JUKEBOX.get();
            case LIGHT_BLUE -> MuftItems.LIGHT_BLUE_POCKET_JUKEBOX.get();
            case BLUE -> MuftItems.BLUE_POCKET_JUKEBOX.get();
            case PURPLE -> MuftItems.PURPLE_POCKET_JUKEBOX.get();
            case MAGENTA -> MuftItems.MAGENTA_POCKET_JUKEBOX.get();
            case PINK -> MuftItems.PINK_POCKET_JUKEBOX.get();
            case null -> MuftItems.POCKET_JUKEBOX.get();
        };
    }

    private static void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertFailSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 1.0F, 1.0F);
    }

    private void onContentChanged(Player user) {
        var screenHandler = user.containerMenu;
        if (screenHandler != null) screenHandler.slotsChanged(user.getInventory());
    }
}