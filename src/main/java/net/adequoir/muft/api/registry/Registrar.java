package net.adequoir.muft.api.registry;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Registrar<T> {
    private static final Consumer<Object> NO_OP = o -> {};
    protected final String namespace;
    protected final Registry<T> registry;

    protected Registrar(String namespace, Registry<T> registry) {
        this.namespace = namespace;
        this.registry = registry;
    }

    public <U extends T> Supplier<U> register(String path, U entry) { return register(Identifier.of(namespace, path), entry); }
    public <U extends T> Supplier<U> register(Identifier id, U entry) { return register(id, Function.identity(), entry); }

    public <S, U extends T> Supplier<U> register(String path, Function<? super S, U> factory, S value) {
        return register(Identifier.of(namespace, path), factory, value);
    }
    public <S, U extends T> Supplier<U> register(Identifier id, Function<? super S, U> factory, S value) {
        return registerWith(id, factory, value, NO_OP);
    }

    public <U extends T> Supplier<U> register(String path, Supplier<U> deferredEntry) {
        return register(Identifier.of(namespace, path), deferredEntry);
    }
    public <U extends T> Supplier<U> register(Identifier id, Supplier<U> deferredEntry) {
        return register(id, (Function<Supplier<U>, U>) Supplier::get, deferredEntry);
    }

    public <S, U extends T> Supplier<U> register(String path, Function<? super S, U> factory, Supplier<S> deferredValue) {
        return register(Identifier.of(namespace, path), factory, deferredValue);
    }
    public <S, U extends T> Supplier<U> register(Identifier id, Function<? super S, U> factory, Supplier<S> deferredValue) {
        return registerWith(id, factory, deferredValue, NO_OP);
    }

    public <S, U extends T> Supplier<U> registerWith(String path, Function<? super S, U> factory, S value, Consumer<? super U> callback) {
        return registerWith(Identifier.of(namespace, path), factory, value, callback);
    }
    public abstract <S, U extends T> Supplier<U> registerWith(Identifier id, Function<? super S, U> factory, S value, Consumer<? super U> callback);

    public <S, U extends T> Supplier<U> registerWith(String path, Function<? super S, U> factory, Supplier<S> value, Consumer<? super U> callback) {
        return registerWith(Identifier.of(namespace, path), factory, value, callback);
    }
    public abstract <S, U extends T> Supplier<U> registerWith(Identifier id, Function<? super S, U> factory, Supplier<S> value, Consumer<? super U> callback);
}
