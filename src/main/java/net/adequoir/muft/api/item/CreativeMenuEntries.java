package net.adequoir.muft.api.item;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

public abstract class CreativeMenuEntries {
    private static @Nullable CreativeMenuEntries instance;

    protected abstract void insertAfter(ResourceKey<CreativeModeTab> itemGroup, ItemLike anchor, Supplier<? extends ItemLike>[] itemSuppliers);
    protected abstract void insertBefore(ResourceKey<CreativeModeTab> itemGroup, ItemLike anchor, Supplier<? extends ItemLike>[] itemSuppliers);


    @SafeVarargs
    public static void addAfter(ResourceKey<CreativeModeTab> itemGroup, ItemLike anchor, Supplier<? extends ItemLike>... itemSuppliers) {
        if (instance != null) { instance.insertAfter(itemGroup, anchor, itemSuppliers); return; }
        throwNotBootstrapped();
    }

    @SafeVarargs
    public static void addBefore(ResourceKey<CreativeModeTab> itemGroup, ItemLike anchor, Supplier<? extends ItemLike>... itemSuppliers) {
        if (instance != null) { instance.insertBefore(itemGroup, anchor, itemSuppliers); return; }
        throwNotBootstrapped();
    }

    public static void bootstrap(CreativeMenuEntries entries) {
        if (instance == null) { instance = entries; return; }
        throw new IllegalStateException("CreativeMenuEntries already bootstrapped!");
    }

    protected CreativeMenuEntries() {}

    private static void throwNotBootstrapped() { throw new IllegalStateException("CreativeMenuEntries has not been bootstrapped"); }
}
