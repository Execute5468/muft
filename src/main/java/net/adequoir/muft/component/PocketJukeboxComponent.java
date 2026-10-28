package net.adequoir.muft.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.adequoir.muft.registry.MuftComponentTypes;
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

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class PocketJukeboxComponent implements TooltipData {
    public static final int MAX_COLUMN_LENGTH = 4;
    public static final int MAX_ROW_LENGTH = 5;
    public static final int MAX_SLOT_COUNT = MAX_COLUMN_LENGTH * MAX_ROW_LENGTH;
    private static final ItemStack[] EMPTY_STACKS = new ItemStack[0];
    private static final int FULL_ITEM_BAR_COLOR = 0xFFFF5454;
    private static final int ITEM_BAR_COLOR = 0xFF7188FF;

    public static final PocketJukeboxComponent DEFAULT = new PocketJukeboxComponent(EMPTY_STACKS);
    public static final Codec<PocketJukeboxComponent> CODEC = ItemStack.CODEC.listOf().flatXmap(
         PocketJukeboxComponent::serialize,
         component -> DataResult.success(Arrays.asList(component.stacks))
    );
    public static final PacketCodec<RegistryByteBuf, PocketJukeboxComponent> PACKET_CODEC = ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()).xmap(
        list -> new PocketJukeboxComponent(list.toArray(EMPTY_STACKS)),
        component -> Arrays.asList(component.stacks)
    );
    private static final Pair<PocketJukeboxComponent, @Nullable ItemStack> EMPTY_REMOVED = new Pair<>(DEFAULT, null);

    private final ItemStack[] stacks;
    private final int selectedIndex;

    private PocketJukeboxComponent(ItemStack[] stacks, int selectedIndex) {
        this.stacks = stacks;
        this.selectedIndex = selectedIndex;
    }
    private PocketJukeboxComponent(ItemStack[] stacks) { this(stacks, -1); }

    public int size() { return stacks.length; }
    public boolean isFull() { return stacks.length >= MAX_SLOT_COUNT; }
    public boolean isEmpty() { return stacks.length == 0; }
    public boolean hasSelected() { return selectedIndex >= 0; }

    public int selectedIndex() { return this.selectedIndex; }

    public ItemStack getSelected() { return hasSelected() ? ItemStack.EMPTY : get(selectedIndex).copy(); }

    public ItemStack get(int index) {
        if (inBounds(index)) return stacks[index].copy();
        throw new IndexOutOfBoundsException(index);
    }

    public static PocketJukeboxComponent get(ItemStack stack) {
        return stack.getOrDefault(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS, DEFAULT);
    }

    public static @Nullable PocketJukeboxComponent getOrNull(ItemStack stack) {
        return stack.get(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS);
    }

    public static int sizeItemBarStep(ItemStack stack) {
        var component = stack.get(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS);
        return component != null ? Math.min(component.size() * 13 / MAX_SLOT_COUNT, 13) : 0;
    }

    public static int colorItemBar(ItemStack stack) {
        var component = stack.getOrDefault(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS, DEFAULT);
        return component.size() >= MAX_SLOT_COUNT ? FULL_ITEM_BAR_COLOR : ITEM_BAR_COLOR;
    }

    public static void select(ItemStack stack, int stackIndex) {
        var component = stack.get(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS);
        if (component == null) return;
        var normalizedIndex = component.normalizeStackIndex(stackIndex);
        var newComponent = new PocketJukeboxComponent(component.stacks, normalizedIndex);
        stack.set(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS, newComponent);
    }

    public static ItemStack getSelected(ItemStack stack) {
        var component = stack.getOrDefault(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS, DEFAULT);
        return component.inBounds(component.selectedIndex) ? component.stacks[component.selectedIndex] : ItemStack.EMPTY;
    }

    private int normalizeStackIndex(int stackIndex) {
        return inBounds(stackIndex) ? stacks.length - 1 : stackIndex % stacks.length;
    }

    private static DataResult<PocketJukeboxComponent> serialize(List<ItemStack> stacks) {
        return DataResult.success(new PocketJukeboxComponent(stacks.toArray(EMPTY_STACKS)));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canInsert(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem().getComponents().contains(MuftComponentTypes.POCKET_JUKEBOX_CONTENTS);
    }

    public PocketJukeboxComponent withAdded(ItemStack stack) {
        if (stack.isEmpty()) return this;
        if (isFull()) throwFull();
        if (!canInsert(stack)) throwUnsupportedStack(stack);
        var lastIndex = stacks.length;
        var newStacks = Arrays.copyOf(stacks, lastIndex + 1);
        newStacks[lastIndex] = stack;

        return new PocketJukeboxComponent(newStacks, selectedIndex);
    }

    public PocketJukeboxComponent withAdded(Slot slot, PlayerEntity player) {
        var stack = slot.getStack();
        if (stack.isEmpty()) return this;
        if (isFull()) throwFull();
        if (!canInsert(stack)) throwUnsupportedStack(stack);
        return this.withAdded(slot.takeStackRange(1, 1, player));
    }

    public PocketJukeboxComponent withSelected(int slotIndex) {
        slotIndex = inBounds(slotIndex) ? slotIndex : -1;
        return new PocketJukeboxComponent(stacks, slotIndex);
    }

    public Pair<PocketJukeboxComponent, @Nullable ItemStack> withRemoved() {
        if (stacks.length == 0) return EMPTY_REMOVED;
        int i = inBounds(selectedIndex) ? selectedIndex : 0;
        var removedStack = stacks[i].copy();
        if (stacks.length == 1) return new Pair<>(DEFAULT, removedStack);
        return new Pair<>(new PocketJukeboxComponent(copyOmit(stacks, i), -1), removedStack);
    }

    private static ItemStack[] copyOmit(ItemStack[] stacks, int omittedIndex) {
        int tailLength = stacks.length - omittedIndex - 1;

        var newStacks = new ItemStack[stacks.length - 1];
        if (omittedIndex > 0) System.arraycopy(stacks, 0, newStacks, 0, omittedIndex);
        if (tailLength > 0) System.arraycopy(stacks, omittedIndex + 1, newStacks, omittedIndex, tailLength);
        return newStacks;
    }

    private boolean inBounds(int stackIndex) { return stackIndex >= 0 && stackIndex < stacks.length; }

    private static void throwFull() { throw new IllegalStateException("Pocket Jukebox is full"); }
    private static void throwUnsupportedStack(ItemStack stack) {
        throw new IllegalArgumentException("Cannot insert stack " + stack + " into Pocket Jukebox");
    }
}