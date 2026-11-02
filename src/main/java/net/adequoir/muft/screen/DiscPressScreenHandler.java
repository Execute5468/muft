package net.adequoir.muft.screen;

import net.adequoir.muft.item.TrackDiscs;
import net.adequoir.muft.registry.MuftBlocks;
import net.adequoir.muft.registry.MuftItems;
import net.adequoir.muft.registry.MuftJukeboxSongTags;
import net.adequoir.muft.registry.MuftScreenHandlers;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryPair;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.BiConsumer;

public class DiscPressScreenHandler extends ScreenHandler {
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

    private final ScreenHandlerContext context;

    private final Inventory input = new SimpleInventory(2) {
        public @Override void markDirty() {
            super.markDirty();
            onContentChanged(this);
        }
    };
    @SuppressWarnings("FieldCanBeLocal")
    private final Inventory output = new SimpleInventory(1);

    private final Slot dyeSlot;
    private final Slot discSlot;
    private final Slot outputSlot;
    private long lastTakeResultTime;
    private int selectedIndex;

    private final List<RegistryEntry<JukeboxSong>> songEntries;

    public DiscPressScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public DiscPressScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(MuftScreenHandlers.DISC_PRESS, syncId);
        this.context = context;
        this.dyeSlot = addSlot(new DyeSlot(input));
        this.discSlot = addSlot(new DiscSlot(input));
        this.outputSlot = addSlot(new OutputSlot(output));

        addPlayerInventorySlots(playerInventory);

        var songLookup = playerInventory.player.getRegistryManager().getWrapperOrThrow(RegistryKeys.JUKEBOX_SONG).getOrThrow(MuftJukeboxSongTags.DISC_PRESSABLE);
        songEntries = songLookup.stream().toList();
    }

    private void addPlayerInventorySlots(PlayerInventory playerInventory) {
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
        onContentChanged(input);
    }

    public Slot discSlot() { return this.discSlot; }
    public Slot dyeSlot() { return this.dyeSlot; }
    public Slot outputSlot() { return this.outputSlot; }
    public List<RegistryEntry<JukeboxSong>> tracks() { return this.songEntries; }


    public @Override void onContentChanged(Inventory inventory) {
        outputSlot.setStack(craftDisc());
        sendContentUpdates();
    }

    private ItemStack craftDisc() {
        if (songEntries.isEmpty()) return ItemStack.EMPTY;
        var discStack = discSlot.getStack();
        if (discStack.isEmpty()) return ItemStack.EMPTY;
        var songKey = songEntries.get(selectedIndex).getKey();
        if (songKey.isEmpty()) return ItemStack.EMPTY;
        var jukeboxComponent = new JukeboxPlayableComponent(new RegistryPair<>(songKey.get()), true);

        var dyeStack = dyeSlot.getStack();
        var dyeColor = dyeStack.getItem() instanceof DyeItem dye ? dye.getColor() : null;

        var resultDisc = TrackDiscs.get(dyeColor);
        var resultStack = resultDisc.getDefaultStack();
        resultStack.set(DataComponentTypes.JUKEBOX_PLAYABLE, jukeboxComponent);
        return resultStack;
    }

    public @Override ItemStack quickMove(PlayerEntity player, int slotId) {
        var stack = ItemStack.EMPTY;
        var slot = slots.get(slotId);

        //noinspection ConstantValue
        if (slot == null || !slot.hasStack()) return stack;
        var otherStack = slot.getStack();
        stack = otherStack.copy();

        if (slotId == outputSlot.id) {
            if (!insertItem(otherStack, INVENTORY_START, HOTBAR_END, true)) return ItemStack.EMPTY;
            slot.onQuickTransfer(otherStack, stack);
        } else if (slotId != dyeSlot.id && slotId != discSlot.id) {
            var otherItem = otherStack.getItem();
            if (otherItem == MuftItems.BLANK_DISC) {
                if (!insertItem(otherStack, discSlot.id, discSlot.id + 1, false)) return ItemStack.EMPTY;
            } else if (otherItem instanceof DyeItem) {
                if (!insertItem(otherStack, dyeSlot.id, dyeSlot.id + 1, false)) return ItemStack.EMPTY;
            } else if (slotId >= INVENTORY_START && slotId < INVENTORY_END) {
                if (!insertItem(otherStack, HOTBAR_START, HOTBAR_END, false)) return ItemStack.EMPTY;
            } else if (slotId >= HOTBAR_START && slotId < HOTBAR_END && !insertItem(otherStack, INVENTORY_START, INVENTORY_END, false)) return ItemStack.EMPTY;
        } else if (!insertItem(otherStack, INVENTORY_START, HOTBAR_END, false)) return ItemStack.EMPTY;

        if (otherStack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else slot.markDirty();

        if (otherStack.getCount() == stack.getCount()) return ItemStack.EMPTY;
        slot.onTakeItem(player, otherStack);
        return stack;
    }

    public @Override boolean canUse(PlayerEntity player) { return canUse(context, player, MuftBlocks.DISC_PRESS); }

    public @Override void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> dropInventory(player, input));
    }

    private static class DyeSlot extends Slot {
        private static final int X_OFFSET = 167;
        private static final int Y_OFFSET = 37;
        private static final int INDEX = 0;

        DyeSlot(Inventory inventory) { super(inventory, INDEX, X_OFFSET, Y_OFFSET); }
        public @Override boolean canInsert(ItemStack stack) { return stack.getItem() instanceof DyeItem; }
    }

    private static class DiscSlot extends Slot {
        private static final int X_OFFSET = 200;
        private static final int Y_OFFSET = 37;
        private static final int INDEX = 1;

        DiscSlot(Inventory inventory) { super(inventory, INDEX, X_OFFSET, Y_OFFSET); }
        public @Override boolean canInsert(ItemStack stack) { return stack.getItem() == MuftItems.BLANK_DISC; }
    }

    private class OutputSlot extends Slot {
        private static final int X_OFFSET = 256;
        private static final int Y_OFFSET = 37;
        private static final int INDEX = 0;

        private final BiConsumer<World, BlockPos> runner = (world, pos) -> {
            long l = world.getTime();
            if (DiscPressScreenHandler.this.lastTakeResultTime == l) return;
            world.playSound(null, pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            DiscPressScreenHandler.this.lastTakeResultTime = l;
        };

        OutputSlot(Inventory inventory) { super(inventory, INDEX, X_OFFSET, Y_OFFSET); }

        public @Override boolean canInsert(ItemStack stack) { return false; }

        public @Override void onTakeItem(PlayerEntity player, ItemStack stack) {
            DiscPressScreenHandler.this.discSlot.takeStack(1);
            DiscPressScreenHandler.this.dyeSlot.takeStack(1);
            context.run(runner);
            super.onTakeItem(player, stack);
        }
    }
}