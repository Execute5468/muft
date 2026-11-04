package net.adequoir.muft.api.registry;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public abstract class Registrar<T> {
    private static final Consumer<Object> NO_OP = o -> {};
    protected final String namespace;
    protected final Registry<T> registry;

    protected Registrar(String namespace, Registry<T> registry) {
        this.namespace = namespace;
        this.registry = registry;
    }

    public <U extends T> Supplier<U> register(String path, U entry) { return register(ResourceLocation.fromNamespaceAndPath(namespace, path), entry); }
    public <U extends T> Supplier<U> register(ResourceLocation id, U entry) { return register(id, Function.identity(), entry); }

    public <S, U extends T> Supplier<U> register(String path, Function<? super S, U> factory, S value) {
        return register(ResourceLocation.fromNamespaceAndPath(namespace, path), factory, value);
    }
    public <S, U extends T> Supplier<U> register(ResourceLocation id, Function<? super S, U> factory, S value) {
        return registerWith(id, factory, value, NO_OP);
    }

    public <U extends T> Supplier<U> register(String path, Supplier<U> deferredEntry) {
        return register(ResourceLocation.fromNamespaceAndPath(namespace, path), deferredEntry);
    }
    public <U extends T> Supplier<U> register(ResourceLocation id, Supplier<U> deferredEntry) {
        return register(id, (Function<Supplier<U>, U>) Supplier::get, deferredEntry);
    }

    public <S, U extends T> Supplier<U> register(String path, Function<? super S, U> factory, Supplier<S> deferredValue) {
        return register(ResourceLocation.fromNamespaceAndPath(namespace, path), factory, deferredValue);
    }
    public <S, U extends T> Supplier<U> register(ResourceLocation id, Function<? super S, U> factory, Supplier<S> deferredValue) {
        return registerWith(id, factory, deferredValue, NO_OP);
    }

    public <S, U extends T> Supplier<U> registerWith(String path, Function<? super S, U> factory, S value, Consumer<? super U> callback) {
        return registerWith(ResourceLocation.fromNamespaceAndPath(namespace, path), factory, value, callback);
    }
    public abstract <S, U extends T> Supplier<U> registerWith(ResourceLocation id, Function<? super S, U> factory, S value, Consumer<? super U> callback);

    public <S, U extends T> Supplier<U> registerWith(String path, Function<? super S, U> factory, Supplier<S> value, Consumer<? super U> callback) {
        return registerWith(ResourceLocation.fromNamespaceAndPath(namespace, path), factory, value, callback);
    }
    public abstract <S, U extends T> Supplier<U> registerWith(ResourceLocation id, Function<? super S, U> factory, Supplier<S> value, Consumer<? super U> callback);
}
