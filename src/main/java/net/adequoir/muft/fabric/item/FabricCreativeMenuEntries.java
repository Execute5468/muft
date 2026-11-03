package net.adequoir.muft.fabric.item;

import net.adequoir.muft.item.api.CreativeMenuEntries;
import net.adequoir.muft.util.ArrayUtil;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;

import java.util.function.Supplier;

public final class FabricCreativeMenuEntries extends CreativeMenuEntries {
    protected @Override void insertAfter(RegistryKey<ItemGroup> itemGroup, ItemConvertible anchor, Supplier<? extends ItemConvertible>[] itemSuppliers) {
        var items = ArrayUtil.mapTo(itemSuppliers, Supplier::get, ItemConvertible.class);
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.addAfter(anchor, items));
    }

    protected @Override void insertBefore(RegistryKey<ItemGroup> itemGroup, ItemConvertible anchor, Supplier<? extends ItemConvertible>[] itemSuppliers) {
        var items = ArrayUtil.mapTo(itemSuppliers, Supplier::get, ItemConvertible.class);
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.addBefore(anchor, items));
    }
}
