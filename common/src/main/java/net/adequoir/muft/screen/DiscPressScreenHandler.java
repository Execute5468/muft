package net.adequoir.muft.screen;

import net.adequoir.muft.item.TrackDiscs;
import net.adequoir.muft.registry.MuftBlocks;
import net.adequoir.muft.registry.MuftItems;
import net.adequoir.muft.registry.tag.MuftJukeboxSongTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.adequoir.muft.registry.MuftSHandlers;
import java.util.List;
import java.util.function.BiConsumer;

public class DiscPressScreenHandler extends AbstractContainerMenu {
    private static final int INV_OFFSET_X = 140;
    private static final int INV_OFFSET_Y = 84;
    private static final int HOTBAR_OFFSET_Y = 142;
    private static final int SLOT_SIZE = 18;
    private static final int SLOTS_PER_ROW = 9;
    private static final int SLOTS_PER_COLUMN = 3;

    private static final int INVENTORY_START = 3;
    private static final int INVENTORY_END = 3 + (SLOTS_PER_ROW * SLOTS_PER_COLUMN);
    private static final int HOTBAR_START = INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + SLOTS_PER_ROW;

    private final ContainerLevelAccess context;

    private final Container input = new SimpleContainer(2) {
        public @Override void setChanged() {
            super.setChanged();
            slotsChanged(this);
        }
    };
    @SuppressWarnings("FieldCanBeLocal")
    private final Container output = new SimpleContainer(1);

    private final Slot dyeSlot;
    private final Slot discSlot;
    private final Slot outputSlot;
    private long lastTakeResultTime;
    private int selectedIndex;

    private final List<Holder<JukeboxSong>> songEntries;

    public DiscPressScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public DiscPressScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(MuftSHandlers.DISC_PRESS.get(), syncId);
        this.context = context;
        this.dyeSlot = addSlot(new DyeSlot(input));
        this.discSlot = addSlot(new DiscSlot(input));
        this.outputSlot = addSlot(new OutputSlot(output));

        addPlayerInventorySlots(playerInventory);

        var songLookup = playerInventory.player.registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG).getOrThrow(MuftJukeboxSongTags.DISC_PRESSABLE);
        songEntries = songLookup.stream().toList();
    }

    private void addPlayerInventorySlots(Inventory playerInventory) {
        for (int i = 0; i < SLOTS_PER_COLUMN; i++) {
            for (int j = 0; j < SLOTS_PER_ROW; j++) {
                var slot = new Slot(
                    playerInventory,
                    j + i * SLOTS_PER_ROW + SLOTS_PER_ROW,
                    INV_OFFSET_X + j * SLOT_SIZE,
                    INV_OFFSET_Y + i * SLOT_SIZE
                );
                addSlot(slot);
            }
        }

        for (int i = 0; i < SLOTS_PER_ROW; i++) addSlot(new Slot(playerInventory, i, INV_OFFSET_X + i * SLOT_SIZE, HOTBAR_OFFSET_Y));
    }

    public int getSelectedIndex() { return selectedIndex; }
    public void setSelectedIndex(int index) {
        selectedIndex = index < 0 || index >= songEntries.size() ? 0 : index;
        slotsChanged(input);
    }

    public Slot discSlot() { return this.discSlot; }
    public Slot dyeSlot() { return this.dyeSlot; }
    public Slot outputSlot() { return this.outputSlot; }
    public List<Holder<JukeboxSong>> tracks() { return this.songEntries; }


    public @Override void slotsChanged(Container inventory) {
        outputSlot.setByPlayer(craftDisc());
        broadcastChanges();
    }

    private ItemStack craftDisc() {
        if (songEntries.isEmpty()) return ItemStack.EMPTY;
        var discStack = discSlot.getItem();
        if (discStack.isEmpty()) return ItemStack.EMPTY;
        var songKey = songEntries.get(selectedIndex).unwrapKey();
        if (songKey.isEmpty()) return ItemStack.EMPTY;
        var jukeboxComponent = new JukeboxPlayable(new EitherHolder<>(songKey.get()), true);

        var dyeStack = dyeSlot.getItem();
        var dyeColor = dyeStack.getItem() instanceof DyeItem dye ? dye.getDyeColor() : null;

        var resultDisc = TrackDiscs.get(dyeColor);
        var resultStack = resultDisc.getDefaultInstance();
        resultStack.set(DataComponents.JUKEBOX_PLAYABLE, jukeboxComponent);
        return resultStack;
    }

    public @Override ItemStack quickMoveStack(Player player, int slotId) {
        var stack = ItemStack.EMPTY;
        var slot = slots.get(slotId);

        //noinspection ConstantValue
        if (slot == null || !slot.hasItem()) return stack;
        var otherStack = slot.getItem();
        stack = otherStack.copy();

        if (slotId == outputSlot.index) {
            if (!moveItemStackTo(otherStack, INVENTORY_START, HOTBAR_END, true)) return ItemStack.EMPTY;
            slot.onQuickCraft(otherStack, stack);
        } else if (slotId != dyeSlot.index && slotId != discSlot.index) {
            var otherItem = otherStack.getItem();
            if (otherItem == MuftItems.BLANK_DISC.get()) {
                if (!moveItemStackTo(otherStack, discSlot.index, discSlot.index + 1, false)) return ItemStack.EMPTY;
            } else if (otherItem instanceof DyeItem) {
                if (!moveItemStackTo(otherStack, dyeSlot.index, dyeSlot.index + 1, false)) return ItemStack.EMPTY;
            } else if (slotId >= INVENTORY_START && slotId < INVENTORY_END) {
                if (!moveItemStackTo(otherStack, HOTBAR_START, HOTBAR_END, false)) return ItemStack.EMPTY;
            } else if (slotId >= HOTBAR_START && slotId < HOTBAR_END && !moveItemStackTo(otherStack, INVENTORY_START, INVENTORY_END, false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(otherStack, INVENTORY_START, HOTBAR_END, false)) return ItemStack.EMPTY;

        if (otherStack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else slot.setChanged();

        if (otherStack.getCount() == stack.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, otherStack);
        return stack;
    }

    public @Override boolean stillValid(Player player) { return stillValid(context, player, MuftBlocks.DISC_PRESS.get()); }

    public @Override void removed(Player player) {
        super.removed(player);
        this.context.execute((world, pos) -> clearContainer(player, input));
    }

    private static class DyeSlot extends Slot {
        private static final int X_OFFSET = 167;
        private static final int Y_OFFSET = 37;
        private static final int INDEX = 0;

        DyeSlot(Container inventory) { super(inventory, INDEX, X_OFFSET, Y_OFFSET); }
        public @Override boolean mayPlace(ItemStack stack) { return stack.getItem() instanceof DyeItem; }
    }

    private static class DiscSlot extends Slot {
        private static final int X_OFFSET = 200;
        private static final int Y_OFFSET = 37;
        private static final int INDEX = 1;

        DiscSlot(Container inventory) { super(inventory, INDEX, X_OFFSET, Y_OFFSET); }
        public @Override boolean mayPlace(ItemStack stack) { return stack.getItem() == MuftItems.BLANK_DISC.get(); }
    }

    private class OutputSlot extends Slot {
        private static final int X_OFFSET = 256;
        private static final int Y_OFFSET = 37;
        private static final int INDEX = 0;

        private final BiConsumer<Level, BlockPos> runner = (world, pos) -> {
            long l = world.getGameTime();
            if (DiscPressScreenHandler.this.lastTakeResultTime == l) return;
            world.playSound(null, pos, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            DiscPressScreenHandler.this.lastTakeResultTime = l;
        };

        OutputSlot(Container inventory) { super(inventory, INDEX, X_OFFSET, Y_OFFSET); }

        public @Override boolean mayPlace(ItemStack stack) { return false; }

        public @Override void onTake(Player player, ItemStack stack) {
            DiscPressScreenHandler.this.discSlot.remove(1);
            DiscPressScreenHandler.this.dyeSlot.remove(1);
            context.execute(runner);
            super.onTake(player, stack);
        }
    }
}