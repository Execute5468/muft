package net.adequoir.muft.item;

import net.adequoir.muft.api.item.CreativeMenuEntries;
import net.adequoir.muft.util.ArrayUtil;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import java.util.function.Supplier;

public final class FabricCreativeMenuEntries extends CreativeMenuEntries {
    protected @Override void insertAfter(ResourceKey<CreativeModeTab> itemGroup, ItemLike anchor, Supplier<? extends ItemLike>[] itemSuppliers) {
        var items = ArrayUtil.mapTo(itemSuppliers, Supplier::get, ItemLike.class);
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.addAfter(anchor, items));
    }

    protected @Override void insertBefore(ResourceKey<CreativeModeTab> itemGroup, ItemLike anchor, Supplier<? extends ItemLike>[] itemSuppliers) {
        var items = ArrayUtil.mapTo(itemSuppliers, Supplier::get, ItemLike.class);
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.addBefore(anchor, items));
    }
}
