package net.adequoir.muft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.Nullable;

public final class ItemEntry {
    @SuppressWarnings("deprecation")
    public static final Codec<ItemEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemStack.ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(entry -> entry.stack.getItem().builtInRegistryHolder()),
        Codec.intRange(1, 99).fieldOf("count").orElse(1).forGetter(ItemEntry::count),
        DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(entry -> entry.stack.getComponentsPatch())
    ).apply(instance, ItemEntry::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemEntry> PACKET_CODEC = ItemStack.STREAM_CODEC.map(ItemEntry::of, ItemEntry::_unsafe_getOriginalStack);

    public static final ItemEntry EMPTY = new ItemEntry(ItemStack.EMPTY);

    private final ItemStack stack;

    private ItemEntry(Holder<Item> item, int count, DataComponentPatch changes) { this(new ItemStack(item, count, changes)); }
    private ItemEntry(ItemStack stack) { this.stack = stack; }

    public static ItemEntry of(ItemStack stack) { return stack.isEmpty() ? EMPTY : new ItemEntry(stack.copy()); }
    public static ItemStack _unsafe_getOriginalStack(ItemEntry entry) { return entry.stack; }

    public static boolean areStackable(ItemEntry first, ItemEntry second) { return ItemStack.isSameItemSameComponents(first.stack, second.stack); }

    public boolean isEmpty() { return stack.isEmpty(); }
    public boolean isStackable(ItemStack stack) { return ItemStack.isSameItemSameComponents(this.stack, stack); }

    public boolean isOf(Item item) { return item() == item; }
    public boolean isIn(TagKey<Item> tag) { return stack.is(tag); }

    public Item item() { return stack.getItem(); }
    public int count() { return stack.getCount(); }
    public @Nullable<T> T get(DataComponentType<? extends T> type) { return stack.get(type); }

    public Component name() { return stack.getHoverName(); }
    public String translationKey() { return stack.getDescriptionId(); }
    public Rarity rarity() { return stack.getRarity(); }

    public ItemStack toStack() { return stack.copy(); }

    public @Override int hashCode() { return ItemStack.hashItemAndComponents(stack); }
    public @Override boolean equals(Object other) {
        if (other == this) return true;
        return other instanceof ItemEntry entry && ItemStack.matches(stack, entry.stack);
    }
    public @Override String toString() { return stack.getCount() + " x " + stack .getItem(); }
}
