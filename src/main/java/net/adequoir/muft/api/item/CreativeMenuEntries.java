package net.adequoir.muft.api.item;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class CreativeMenuEntries {
    private static @Nullable CreativeMenuEntries instance;

    protected abstract void insertAfter(RegistryKey<ItemGroup> itemGroup, ItemConvertible anchor, Supplier<? extends ItemConvertible>[] itemSuppliers);
    protected abstract void insertBefore(RegistryKey<ItemGroup> itemGroup, ItemConvertible anchor, Supplier<? extends ItemConvertible>[] itemSuppliers);


    @SafeVarargs
    public static void addAfter(RegistryKey<ItemGroup> itemGroup, ItemConvertible anchor, Supplier<? extends ItemConvertible>... itemSuppliers) {
        if (instance != null) { instance.insertAfter(itemGroup, anchor, itemSuppliers); return; }
        throwNotBootstrapped();
    }

    @SafeVarargs
    public static void addBefore(RegistryKey<ItemGroup> itemGroup, ItemConvertible anchor, Supplier<? extends ItemConvertible>... itemSuppliers) {
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
