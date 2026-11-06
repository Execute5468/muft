package net.adequoir.muft.api.item;

import java.util.function.Supplier;

import net.adequoir.muft.api.Bootstrapper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

public abstract class CreativeMenuEntries {
    private static final CreativeMenuEntries INSTANCE = Bootstrapper.instance().creativeMenuEntries();

    protected abstract void insertAfter(ResourceKey<CreativeModeTab> itemGroup, ItemLike anchor, Supplier<? extends ItemLike>[] itemSuppliers);
    protected abstract void insertBefore(ResourceKey<CreativeModeTab> itemGroup, ItemLike anchor, Supplier<? extends ItemLike>[] itemSuppliers);


    @SafeVarargs
    public static void addAfter(ResourceKey<CreativeModeTab> itemGroup, ItemLike anchor, Supplier<? extends ItemLike>... itemSuppliers) {
        INSTANCE.insertAfter(itemGroup, anchor, itemSuppliers);
    }

    @SafeVarargs
    public static void addBefore(ResourceKey<CreativeModeTab> itemGroup, ItemLike anchor, Supplier<? extends ItemLike>... itemSuppliers) {
        INSTANCE.insertBefore(itemGroup, anchor, itemSuppliers);
    }

    protected CreativeMenuEntries() {}
}
