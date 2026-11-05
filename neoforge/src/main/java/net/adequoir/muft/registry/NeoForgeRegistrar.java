package net.adequoir.muft.registry;

import net.adequoir.muft.api.registry.Registrar;
import net.adequoir.muft.api.registry.RegistrarFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
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
    public static final Function<IEventBus, RegistrarFactory> FACTORY_FUNCTION = Factory::new;
    private static final Map<ResourceKey<? extends Registry<?>>, ArrayList<Holder<?, ?>>> registryHolders = new IdentityHashMap<>();
    private final ArrayList<Holder<?, T>> holders;

    @SuppressWarnings("unchecked")
    public NeoForgeRegistrar(String namespace, Registry<T> registry, IEventBus eventBus) {
        super(namespace, registry);
        this.holders = (ArrayList<Holder<?, T>>) (Object) registryHolders.computeIfAbsent(registry.key(), key -> new ArrayList<>());
    }

    public @Override <S, U extends T> Supplier<U> registerWith(String path, Function<? super S, U> factory, S value, Consumer<? super U> callback) {
        return registerWith(path, factory, (Supplier<S>) () -> value, callback);
    }

    @SuppressWarnings("unchecked")
    public @Override <S, U extends T> Supplier<U> registerWith(String path, Function<? super S, U> factory, Supplier<S> value, Consumer<? super U> callback) {
        var holder = new Holder<>(registry, namespace, path, factory, value, callback);
        holders.add(holder);
        return (Supplier<U>) holder;
    }

    @SubscribeEvent
    public static void initialize(RegisterEvent event) {
        var holders = registryHolders.get(event.getRegistryKey());
        if (holders != null) initialize(event, holders);
    }

    @SuppressWarnings("unchecked")
    private static <T> void initialize(RegisterEvent event, ArrayList<? extends Holder<?, ?>> holderList) {
        event.register((ResourceKey<? extends Registry<T>>) event.getRegistryKey(), helper -> {
            var holders = (ArrayList<Holder<?, T>>) holderList;
            for (var holder: holders) holder.initialize(event, helper);
        });
    }

    private static class Holder<S, T> implements Supplier<T> {
        private @Nullable T entry;
        private @Nullable EntryFactory<S, T> factory;
        private @Nullable Supplier<T> supplier;

        @SuppressWarnings("unchecked")
        public <U extends T> Holder(
            Registry<T> registry,
            String namespace, String path,
            Function<? super S, U> factory, Supplier<S> value,
            Consumer<? super U> callback
        ) {
            var id = ResourceLocation.fromNamespaceAndPath(namespace, path);
            var key = ResourceKey.create(registry.key(), id);
            this.factory = new EntryFactory<>(key, (Function<? super S, T>) factory, value, (Consumer<T>) callback);
        }

        @SuppressWarnings("unchecked")
        private void initialize(RegisterEvent event, RegisterEvent.RegisterHelper<T> helper) {
            if (factory == null) return;

            var key = (ResourceKey<T>) factory.key;
            var registry = ((Registry<T>) event.getRegistry());
            boolean existing = registry.containsKey(key);

            if (!existing) {
                entry = factory.factory.apply(factory.value.get());
                factory.callback.accept(entry);
                helper.register(key, entry);
            } else supplier = () -> Objects.requireNonNull(registry.get(key));
            factory = null;
        }

        public @Override T get() {
            if (entry != null) return entry;
            if (supplier != null) {
                entry = supplier.get();
                supplier = null;
                return entry;
            }
            throw new IllegalStateException("Holder is not initialized!");
        }
    }

    private record EntryFactory<S, T>(
        ResourceKey<? super T> key,
        Function<? super S, T> factory,
        Supplier<S> value,
        Consumer<T> callback
    ) {}

    private record Factory(IEventBus eventBus) implements RegistrarFactory {
        public @Override <T> Registrar<T> create(String namespace, Registry<T> registry) {
            return new NeoForgeRegistrar<>(namespace, registry, eventBus);
        }
    }
}
