package net.adequoir.muft.fabric.registry;

import com.mojang.logging.LogUtils;
import net.adequoir.muft.api.registry.Registrar;
import net.adequoir.muft.api.registry.RegistrarFactory;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FabricRegistrar<T> extends Registrar<T> {
    public static final RegistrarFactory FACTORY = FabricRegistrar::new;
    private static final Logger LOGGER = LogUtils.getLogger();

    FabricRegistrar(String namespace, Registry<T> registry) { super(namespace, registry); }

    @SuppressWarnings("unchecked")
    public @Override <S, U extends T> Supplier<U> registerWith(Identifier id, Function<? super S, U> factory, S value, Consumer<? super U> callback) {
        var key = RegistryKey.of(registry.getKey(), id);
        var existing = registry.get(key);
        if (existing != null) {
            LOGGER.info("Entry with key '{}' already exists, skipping registration", key);
            return supplierOf((U) existing);
        }
        var entry = Registry.register(registry, key, factory.apply(value));
        callback.accept(entry);
        return supplierOf(entry);
    }

    public @Override <S, U extends T> Supplier<U> registerWith(Identifier id, Function<? super S, U> factory, Supplier<S> deferredValue, Consumer<? super U> callback) {
        return registerWith(id, factory, deferredValue.get(), callback);
    }

    private static <T> Supplier<T> supplierOf(T entry) { return () -> entry; }
}