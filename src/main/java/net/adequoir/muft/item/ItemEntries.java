package net.adequoir.muft.item;

import net.adequoir.muft.util.ArrayUtil;
import net.adequoir.muft.util.FabricDependant;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;

import java.util.function.Supplier;

@FabricDependant
public final class ItemEntries {

    @SafeVarargs
    public static void addAfter(RegistryKey<ItemGroup> itemGroup, ItemConvertible anchor, Supplier<? extends ItemConvertible>... itemSuppliers) {
        var items = ArrayUtil.mapTo(itemSuppliers, Supplier::get, ItemConvertible.class);
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.addAfter(anchor, items));
    }

    @SafeVarargs
    public static void addBefore(RegistryKey<ItemGroup> itemGroup, ItemConvertible anchor, Supplier<? extends ItemConvertible>... itemSuppliers) {
        var items = ArrayUtil.mapTo(itemSuppliers, Supplier::get, ItemConvertible.class);
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.addBefore(anchor, items));
    }

    private ItemEntries() {}
}
