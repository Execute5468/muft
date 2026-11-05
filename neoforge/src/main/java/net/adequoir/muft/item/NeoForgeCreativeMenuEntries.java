package net.adequoir.muft.item;

import net.adequoir.muft.Muft;
import net.adequoir.muft.api.item.CreativeMenuEntries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

@NotNullByDefault
@EventBusSubscriber(modid = Muft.MOD_ID)
public final class NeoForgeCreativeMenuEntries extends CreativeMenuEntries {
    private static final Map<ResourceKey<CreativeModeTab>, ArrayList<Entry>> entriesMap = new IdentityHashMap<>();

    protected @Override void insertAfter(ResourceKey<CreativeModeTab> itemGroup, ItemLike anchor, Supplier<? extends ItemLike>[] itemSuppliers) {
        var list = entriesMap.computeIfAbsent(itemGroup, key -> new ArrayList<>());
        list.add(new AfterEntry(anchor, itemSuppliers));
    }

    protected @Override void insertBefore(ResourceKey<CreativeModeTab> itemGroup, ItemLike anchor, Supplier<? extends ItemLike>[] itemSuppliers) {
        var list = entriesMap.computeIfAbsent(itemGroup, key -> new ArrayList<>());
        list.add(new BeforeEntry(anchor, itemSuppliers));
    }

    @SubscribeEvent
    public static void initialize(BuildCreativeModeTabContentsEvent event) {
        var tabKey = event.getTabKey();
        var entries = entriesMap.get(tabKey);
        if (entries == null) return;
        for (var entry: entries) entry.accept(event);
    }

    private interface Entry { void accept(BuildCreativeModeTabContentsEvent event);}

    private record AfterEntry(ItemLike anchor, Supplier<? extends ItemLike>[] itemSuppliers) implements Entry {
        public @Override void accept(BuildCreativeModeTabContentsEvent event) {
            for (var supplier : itemSuppliers) {
                var stack = supplier.get().asItem().getDefaultInstance();
                event.remove(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                event.insertAfter(
                    anchor.asItem().getDefaultInstance(),
                    stack,
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
                );
            }
        }
    }
    private record BeforeEntry(ItemLike anchor, Supplier<? extends ItemLike>[] itemSuppliers) implements Entry {
        public @Override void accept(BuildCreativeModeTabContentsEvent event) {
            for (var supplier : itemSuppliers) {
                var stack = supplier.get().asItem().getDefaultInstance();
                event.remove(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                event.insertBefore(
                    anchor.asItem().getDefaultInstance(),
                    stack,
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
                );
            }
        }
    }
}
