package net.adequoir.muft.registry;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import net.adequoir.muft.api.registry.Registrar;
import net.adequoir.muft.api.registry.RegistrarFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNullByDefault;
import org.slf4j.Logger;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@NotNullByDefault
public final class FabricRegistrar<T> extends Registrar<T> {
    public static final RegistrarFactory FACTORY = FabricRegistrar::new;
    private static final Logger LOGGER = LogUtils.getLogger();

    FabricRegistrar(String namespace, Registry<T> registry) { super(namespace, registry); }

    @SuppressWarnings("unchecked")
    public @Override <S, U extends T> Supplier<U> registerWith(String path, Function<? super S, U> factory, S value, Consumer<? super U> callback) {
        var key = ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(namespace, path));
        var existing = registry.containsKey(key);
        if (existing) {
            LOGGER.info("Entry with key '{}' already exists, skipping registration", key);
            return Suppliers.memoize(() -> (U) registry.get(key));
        }
        var entry = Registry.register(registry, key, factory.apply(value));
        callback.accept(entry);
        return supplierOf(entry);
    }

    public @Override <S, U extends T> Supplier<U> registerWith(String path, Function<? super S, U> factory, Supplier<S> deferredValue, Consumer<? super U> callback) {
        return registerWith(path, factory, deferredValue.get(), callback);
    }

    private static <T> Supplier<T> supplierOf(T entry) { return () -> entry; }
}