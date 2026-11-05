package net.adequoir.muft.api.registry;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;

public abstract class Registrar<T> {
    private static final Consumer<Object> NO_OP = o -> {};
    protected final String namespace;
    protected final Registry<T> registry;

    protected Registrar(String namespace, Registry<T> registry) {
        this.namespace = namespace;
        this.registry = registry;
    }

    public <U extends T> Supplier<U> register(String path, U entry) { return register(path, Function.identity(), entry); }

    public <S, U extends T> Supplier<U> register(String path, Function<? super S, U> factory, S value) {
        return registerWith(path, factory, value, NO_OP);
    }

    public <U extends T> Supplier<U> register(String path, Supplier<U> deferredEntry) {
        return registerWith(path, Function.identity(), deferredEntry, NO_OP);
    }

    public <S, U extends T> Supplier<U> register(String path, Function<? super S, U> factory, Supplier<S> deferredValue) {
        return registerWith(path, factory, deferredValue, NO_OP);
    }

    public abstract  <S, U extends T> Supplier<U> registerWith(String path, Function<? super S, U> factory, S value, Consumer<? super U> callback);

    public abstract  <S, U extends T> Supplier<U> registerWith(String path, Function<? super S, U> factory, Supplier<S> value, Consumer<? super U> callback);
}
