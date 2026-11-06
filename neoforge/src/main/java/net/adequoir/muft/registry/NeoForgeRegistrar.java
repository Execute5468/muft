package net.adequoir.muft.registry;

import net.adequoir.muft.api.registry.Registrar;
import net.adequoir.muft.api.registry.RegistrarFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@NotNullByDefault
public final class NeoForgeRegistrar<T> extends Registrar<T> {
    public static final RegistrarFactory FACTORY = NeoForgeRegistrar::new;
    private static final Map<ResourceKey<? extends Registry<?>>, ArrayList<EntryFactory<?, ?>>> registryFactories = new IdentityHashMap<>();
    private final ArrayList<EntryFactory<?, ? extends T>> factories;

    @SuppressWarnings("unchecked")
    NeoForgeRegistrar(String namespace, Registry<T> registry) {
        super(namespace, registry);
        this.factories = (ArrayList<EntryFactory<?, ? extends T>>) (Object) registryFactories.computeIfAbsent(registry.key(), key -> new ArrayList<>());
    }

    public @Override <S, U extends T> Supplier<U> registerWith(String path, Function<? super S, U> factory, S value, Consumer<? super U> callback) {
        return registerWith(path, factory, (Supplier<S>) () -> value, callback);
    }

    @SuppressWarnings("unchecked")
    public @Override <S, U extends T> Supplier<U> registerWith(String path, Function<? super S, U> factory, Supplier<S> value, Consumer<? super U> callback) {
        var ref = new EntryRef<U>();

        var key = ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(namespace, path));
        var entryFactory = new EntryFactory<>((ResourceKey<U>) key, factory, value, callback, ref);
        factories.add(entryFactory);
        return ref;
    }

    @SubscribeEvent
    public static void initialize(RegisterEvent event) {
        var factories = registryFactories.get(event.getRegistryKey());
        if (factories != null) initialize(event, factories);
    }

    @SuppressWarnings("unchecked")
    private static <T> void initialize(RegisterEvent event, ArrayList<? extends EntryFactory<?, ?>> holderList) {
        event.register((ResourceKey<? extends Registry<T>>) event.getRegistryKey(), helper -> {
            var holders = (ArrayList<EntryFactory<?, T>>) holderList;

            var itr = holders.iterator();
            while (itr.hasNext()) {
                var factory = itr.next();
                factory.initialize(event, helper);
                itr.remove();
            }
        });
    }

    private static class EntryRef<T> implements Supplier<T> {
        private @Nullable T entry;
        private @Nullable Supplier<T> delegate;

        public @Override T get() {
            if (delegate != null) {
                entry = delegate.get();
                delegate = null;
            }
            if (entry != null) return entry;
            throw new IllegalStateException("EntryRef is not initialized!");
        }
    }

    private record EntryFactory<S, T>(
        ResourceKey<T> key,
        Function<? super S, T> factory,
        Supplier<S> value,
        Consumer<? super T> callback,
        EntryRef<T> entryRef
    ) {

        @SuppressWarnings("unchecked")
        private void initialize(RegisterEvent event, RegisterEvent.RegisterHelper<T> helper) {
            var registry = (Registry<T>) event.getRegistry();
            if (registry.containsKey(key)) {
                entryRef.delegate = () -> Objects.requireNonNull(registry.get(key));
                return;
            }
            var entry = factory.apply(this.value.get());
            helper.register(key, entry);
            entryRef.entry = entry;
            entryRef.delegate = null;
        }
    }
}
