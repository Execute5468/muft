package net.adequoir.muft.api.resource;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class PackHelper {
    private static @Nullable PackHelper instance;

    protected abstract void register(PackStorage storage);

    public static void build(String namespace, Consumer<PackStorage> consumer) {
        var storage = new PackStorage(namespace);
        consumer.accept(storage);

        var instance = PackHelper.instance;
        if (instance != null) { instance.register(storage); return; }
        throw new IllegalStateException("PackHelper has not been bootstrapped!");
    }

    public static void bootstrap(PackHelper helper) {
        if (instance == null) { instance = helper; return; }
        throw new IllegalStateException("PackHelper already bootstrapped!");
    }

    protected PackHelper() {}
}