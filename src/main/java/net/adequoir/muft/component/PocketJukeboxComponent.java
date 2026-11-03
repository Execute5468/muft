package net.adequoir.muft.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.adequoir.muft.registry.MuftComponentTypes;
import net.adequoir.muft.util.ItemEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class PocketJukeboxComponent implements TooltipData {
    public static final int MAX_COLUMN_LENGTH = 4;
    public static final int MAX_ROW_LENGTH = 5;
    public static final int MAX_SLOT_COUNT = MAX_COLUMN_LENGTH * MAX_ROW_LENGTH;
    private static final ItemEntry[] EMPTY_ENTRIES = new ItemEntry[0];
    private static final List<ItemEntry> EMPTY_LIST = List.of();
    private static final int FULL_ITEM_BAR_COLOR = 0xFFFF5454;
    private static final int ITEM_BAR_COLOR = 0xFF7188FF;

    public static final PocketJukeboxComponent DEFAULT = new PocketJukeboxComponent(EMPTY_ENTRIES);
    public static final Codec<PocketJukeboxComponent> CODEC = ItemEntry.CODEC.listOf().flatXmap(
         PocketJukeboxComponent::serialize,
         component -> DataResult.success(component.isEmpty() ? EMPTY_LIST : Arrays.asList(component.entries))
    );
    public static final PacketCodec<RegistryByteBuf, PocketJukeboxComponent> PACKET_CODEC = new PacketCodec<>() {
        private static final PacketCodec<RegistryByteBuf, List<ItemEntry>> ITEM_LIST_CODEC = ItemEntry.PACKET_CODEC.collect(PacketCodecs.toList());

        public @Override PocketJukeboxComponent decode(RegistryByteBuf buf) {
            var entries = ITEM_LIST_CODEC.decode(buf).toArray(EMPTY_ENTRIES);
            return new PocketJukeboxComponent(entries, -1, buf.readVarInt());
        }

        public @Override void encode(RegistryByteBuf buf, PocketJukeboxComponent value) {
            ITEM_LIST_CODEC.encode(buf, Arrays.asList(value.entries));
            buf.writeVarInt(value.playbackId);
        }
    };

    private static final Pair<PocketJukeboxComponent, @Nullable ItemStack> EMPTY_REMOVED = new Pair<>(DEFAULT, null);

    private final ItemEntry[] entries;
    private final int selectedIndex;
    private final int playbackId;

    private PocketJukeboxComponent(ItemEntry[] entries, int selectedIndex, int playbackId) {
        this.entries = entries;
        this.selectedIndex = selectedIndex;
        this.playbackId = playbackId;
    }
    private PocketJukeboxComponent(ItemEntry[] entries) { this(entries, -1, 0); }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canInsert(ItemStack stack) {
        return !stack.isEmpty() && stack.getComponents().contains(DataComponentTypes.JUKEBOX_PLAYABLE);
    }

    public static PocketJukeboxComponent get(ItemStack stack) {
        return stack.getOrDefault(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS.get(), DEFAULT);
    }

    public static @Nullable PocketJukeboxComponent getOrNull(ItemStack stack) {
        return stack.get(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS.get());
    }

    public static void set(ItemStack stack, PocketJukeboxComponent component) {
        stack.set(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS.get(), component);
    }

    public static int sizeItemBarStep(ItemStack stack) {
        var component = stack.get(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS.get());
        return component != null ? Math.min(component.size() * 13 / MAX_SLOT_COUNT, 13) : 0;
    }

    public static int colorItemBar(ItemStack stack) {
        var component = stack.getOrDefault(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS.get(), DEFAULT);
        return component.size() >= MAX_SLOT_COUNT ? FULL_ITEM_BAR_COLOR : ITEM_BAR_COLOR;
    }

    public int size() { return entries.length; }
    public boolean isFull() { return entries.length >= MAX_SLOT_COUNT; }
    public boolean isEmpty() { return entries.length == 0; }
    public boolean hasSelected() { return selectedIndex >= 0; }

    public int selectedIndex() { return selectedIndex; }
    public int boundSelectedIndex() { return inBounds(selectedIndex) ? selectedIndex : 0; }

    public int playbackId() { return this.playbackId; }

    public ItemEntry get(int index) { return entries[index]; }
    public ItemEntry getSelected() { return hasSelected() ? entries[selectedIndex] : ItemEntry.EMPTY; }

    public PocketJukeboxComponent withAdded(ItemStack stack) {
        if (stack.isEmpty()) return this;
        if (isFull()) throwFull();
        if (!canInsert(stack)) throwUnsupportedStack(stack);
        var lastIndex = entries.length;
        var newStacks = Arrays.copyOf(entries, lastIndex + 1);
        newStacks[lastIndex] = ItemEntry.of(stack);

        return new PocketJukeboxComponent(newStacks, selectedIndex, playbackId);
    }

    public PocketJukeboxComponent withAdded(Slot slot, PlayerEntity player) {
        var stack = slot.getStack();
        if (stack.isEmpty()) return this;
        if (isFull()) throwFull();
        if (!canInsert(stack)) throwUnsupportedStack(stack);
        return this.withAdded(slot.takeStackRange(1, 1, player));
    }

    public PocketJukeboxComponent withSelected(int slotIndex) {
        slotIndex = slotIndex < -1 || slotIndex >= size() ? -1: slotIndex;
        return slotIndex != this.selectedIndex ? new PocketJukeboxComponent(entries, slotIndex, playbackId) : this;
    }

    public PocketJukeboxComponent withPlaying(int playbackId) {
        return playbackId == this.playbackId ? this : new PocketJukeboxComponent(entries, selectedIndex, playbackId);
    }

    public Pair<PocketJukeboxComponent, @Nullable ItemStack> withRemoved() {
        if (entries.length == 0) return EMPTY_REMOVED;
        int i = inBounds(selectedIndex) ? selectedIndex : 0;
        var removedStack = entries[i].toStack();
        if (entries.length == 1) return new Pair<>(DEFAULT, removedStack);

        return new Pair<>(new PocketJukeboxComponent(copyOmit(entries, i), -1, playbackId), removedStack);
    }

    public @Override boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof PocketJukeboxComponent jukeboxComponent
                && stacksEqual(entries, jukeboxComponent.entries);
    }

    public @Override int hashCode() {
        int h = 0;
        for (var entry: entries) h = h * 31 + entry.hashCode();
        return h;
    }

    public @Override String toString() { return "PocketJukeboxContents" + Arrays.toString(entries); }

    private static boolean stacksEqual(ItemEntry[] left, ItemEntry[] right) {
        if (left == right) return true;
        if (left.length != right.length) return false;
        for (int i = 0; i < left.length; i++) { if (left[i].equals(right[i])) return false; }
        return true;
    }

    private static ItemEntry[] copyOmit(ItemEntry[] stacks, int omittedIndex) {
        int tailLength = stacks.length - omittedIndex - 1;

        var newStacks = new ItemEntry[stacks.length - 1];
        if (omittedIndex > 0) System.arraycopy(stacks, 0, newStacks, 0, omittedIndex);
        if (tailLength > 0) System.arraycopy(stacks, omittedIndex + 1, newStacks, omittedIndex, tailLength);
        return newStacks;
    }

    public boolean inBounds(int stackIndex) { return stackIndex >= 0 && stackIndex < entries.length; }

    private static DataResult<PocketJukeboxComponent> serialize(List<ItemEntry> entries) {
        if (entries.isEmpty()) return DataResult.success(DEFAULT);
        return DataResult.success(new PocketJukeboxComponent(entries.toArray(EMPTY_ENTRIES)));
    }

    private static void throwFull() { throw new IllegalStateException("Pocket Jukebox is full"); }
    private static void throwUnsupportedStack(ItemStack stack) {
        throw new IllegalArgumentException("Cannot insert stack " + stack + " into Pocket Jukebox");
    }
}