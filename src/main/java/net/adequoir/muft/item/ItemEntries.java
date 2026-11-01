package net.adequoir.muft.item;

import net.adequoir.muft.util.FabricDependant;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;

@FabricDependant
public final class ItemEntries {

    public static void addAfter(RegistryKey<ItemGroup> itemGroup, ItemConvertible anchor, ItemConvertible... items) {
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.addAfter(anchor, items));
    }

    public static void addBefore(RegistryKey<ItemGroup> itemGroup, ItemConvertible anchor, ItemConvertible... items) {
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.addBefore(anchor, items));
    }

    private ItemEntries() {}
}
