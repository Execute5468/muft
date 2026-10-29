package net.adequoir.muft.client.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Objects;

public final class ItemEntry {
    @SuppressWarnings("deprecation")
    public static final Codec<ItemEntry> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.ITEM_CODEC.fieldOf("id").forGetter(entry -> entry.item.getRegistryEntry()),
            Codec.INT.optionalFieldOf("count", 1).forGetter(ItemEntry::count),
            ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(entry -> entry.components.getChanges())
        ).apply(instance, ItemEntry::new)
    ));

    public static final PacketCodec<RegistryByteBuf, ItemEntry> PACKET_CODEC = new PacketCodec<>() {
        private static final PacketCodec<RegistryByteBuf, RegistryEntry<Item>> ITEM_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.ITEM);

        public @Override ItemEntry decode(RegistryByteBuf buf) {
            var item = ITEM_PACKET_CODEC.decode(buf);
            var changes = ComponentChanges.PACKET_CODEC.decode(buf);
            var count = buf.readVarInt();
            var id = buf.readVarInt();
            return Manager.INSTANCE.sample(item, changes, count, id);
        }

        @SuppressWarnings("deprecation")
        public @Override void encode(RegistryByteBuf buf, ItemEntry value) {
            ITEM_PACKET_CODEC.encode(buf, value.item.getRegistryEntry());
            ComponentChanges.PACKET_CODEC.encode(buf, value.components.getChanges());
            buf.writeVarInt(value.count);
            buf.writeVarInt(value.id);
        }
    };

    private final Item item;
    private final ComponentMapImpl components;
    private final int count;
    private final int id;

    private ItemEntry(RegistryEntry<Item> item, ComponentChanges changes, int count, int id) {
        this.item = Objects.requireNonNull(item.value());
        this.components = Objects.requireNonNull(ComponentMapImpl.create(item.value().getComponents(), changes));
        this.count = count;
        this.id = id;
    }

    private ItemEntry(RegistryEntry<Item> item, int count, ComponentChanges changes) {
        this.item = Objects.requireNonNull(item.value());
        this.components = Objects.requireNonNull(ComponentMapImpl.create(item.value().getComponents(), changes));
        this.count = count;
        this.id = Manager.INSTANCE.add(this);
    }

    public ItemEntry(ItemStack stack) {
        this.item = stack.getItem();
        this.components = ((ComponentMapImpl) stack.getComponents()).copy();
        this.count = stack.getCount();
        this.id = Manager.INSTANCE.add(this);
    }

    public Item item() { return item; }
    public int count() { return count; }
    public int id() { return id; }
    public <T> @Nullable T get(ComponentType<? extends T> type) { return components.get(type); }

    @SuppressWarnings("deprecation")
    public ItemStack toItemStack() { return new ItemStack(item.getRegistryEntry(), count, components.getChanges()); }

    public @Override boolean equals(Object other) {
        if (other == this) return true;
        return other instanceof ItemEntry itemEntry
                && item == itemEntry.item
                && count == itemEntry.count
                && components.equals(itemEntry.components);
    }
    public @Override int hashCode() { return 31 * (31 + item.hashCode()) + components.hashCode(); }
    public @Override String toString() { return item + " x " + count; }

    private static final class Manager {
        static final Manager INSTANCE = new Manager();

        private static final int INIT_FREE_SLOTS_CAPACITY = 8;
        private static final int INIT_REFS_CAPACITY = 16;
        private static final EntryRef[] EMPTY_REFS = new EntryRef[0];
        private static final int[] EMPTY_INTS = new int[0];

        private final ReferenceQueue<ItemEntry> queue = new ReferenceQueue<>();

        private @Nullable EntryRef[] refs = EMPTY_REFS;
        private int head = 0;

        private int[] freeSlots = EMPTY_INTS;
        private int numFreeSlots = 0;

        int add(ItemEntry entry) {
            cleanRefs();
            int id = getFreeSlot();
            var ref = new EntryRef(queue, entry, id);
            refs[id] = ref;
            return id;
        }

        ItemEntry sample(RegistryEntry<Item> item, ComponentChanges changes, int count, int id) {
            cleanRefs();
            var ref = refs[id];
            ItemEntry entry;
            if (ref != null && (entry = ref.get()) != null) return entry;
            entry = new ItemEntry(item, changes, count, id);
            consumeSlot(id);
            var newRef = new EntryRef(queue, entry, id);
            refs[id] = newRef;
            return entry;
        }

        private int getFreeSlot() {
            if (numFreeSlots <= 0) { ensureCapacity(); return head++; }
            return freeSlots[--numFreeSlots];
        }

        private void ensureCapacity() {
            int capacity = refs.length;
            if (head < capacity) return;
            int newLength = Math.max(capacity + (capacity >> 1), INIT_REFS_CAPACITY);
            refs = Arrays.copyOf(refs, newLength);
        }

        private void cleanRefs() {
            var queue = this.queue;
            var refs = this.refs;
            @Nullable EntryRef ref;
            while ((ref = (EntryRef) queue.poll()) != null) { refs[ref.id] = null; addFreeSlot(ref.id); }
        }

        private void addFreeSlot(int id) {
            ensureFreeSlots();
            freeSlots[numFreeSlots++] = id;
        }

        private void ensureFreeSlots() {
            int capacity = freeSlots.length;
            if (numFreeSlots < capacity) return;
            var newLength = Math.max(capacity + (capacity >>> 1), INIT_FREE_SLOTS_CAPACITY);
            freeSlots = Arrays.copyOf(freeSlots, newLength);
        }

        private void consumeSlot(int id) {
            var freeSlots = this.freeSlots;
            for (int i = 0; i < freeSlots.length; i++) {
                if (freeSlots[i] != id) continue;
                int tailLength = freeSlots.length - i - 1;
                if (tailLength > 0) System.arraycopy(freeSlots, i + 1, freeSlots, i, tailLength);
                numFreeSlots--;
                return;
            }
        }
    }

    private static final class EntryRef extends WeakReference<ItemEntry> {
        private final int id;

        EntryRef(ReferenceQueue<ItemEntry> queue, ItemEntry entry, int id) {
            super(entry, queue);
            this.id = id;
        }
    }
}
