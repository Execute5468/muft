package net.adequoir.muft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

public final class ItemEntry {
    @SuppressWarnings("deprecation")
    public static final Codec<ItemEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemStack.ITEM_CODEC.fieldOf("id").forGetter(entry -> entry.stack.getItem().getRegistryEntry()),
        Codec.intRange(1, 99).fieldOf("count").orElse(1).forGetter(ItemEntry::count),
        ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(entry -> entry.stack.getComponentChanges())
    ).apply(instance, ItemEntry::new));
    public static final PacketCodec<RegistryByteBuf, ItemEntry> PACKET_CODEC = ItemStack.PACKET_CODEC.xmap(ItemEntry::of, ItemEntry::_unsafe_getOriginalStack);

    public static final ItemEntry EMPTY = new ItemEntry(ItemStack.EMPTY);

    private final ItemStack stack;

    private ItemEntry(RegistryEntry<Item> item, int count, ComponentChanges changes) { this(new ItemStack(item, count, changes)); }
    private ItemEntry(ItemStack stack) { this.stack = stack; }

    public static ItemEntry of(ItemStack stack) { return stack.isEmpty() ? EMPTY : new ItemEntry(stack.copy()); }
    public static ItemStack _unsafe_getOriginalStack(ItemEntry entry) { return entry.stack; }

    public static boolean areStackable(ItemEntry first, ItemEntry second) { return ItemStack.areItemsAndComponentsEqual(first.stack, second.stack); }

    public boolean isEmpty() { return stack.isEmpty(); }
    public boolean isStackable(ItemStack stack) { return ItemStack.areItemsAndComponentsEqual(this.stack, stack); }

    public boolean isOf(Item item) { return item() == item; }
    public boolean isIn(TagKey<Item> tag) { return stack.isIn(tag); }

    public Item item() { return stack.getItem(); }
    public int count() { return stack.getCount(); }
    public @Nullable<T> T get(ComponentType<? extends T> type) { return stack.get(type); }

    public Text name() { return stack.getName(); }
    public String translationKey() { return stack.getTranslationKey(); }
    public Rarity rarity() { return stack.getRarity(); }

    public ItemStack toStack() { return stack.copy(); }

    public @Override int hashCode() { return ItemStack.hashCode(stack); }
    public @Override boolean equals(Object other) {
        if (other == this) return true;
        return other instanceof ItemEntry entry && ItemStack.areEqual(stack, entry.stack);
    }
    public @Override String toString() { return stack.getCount() + " x " + stack .getItem(); }
}
