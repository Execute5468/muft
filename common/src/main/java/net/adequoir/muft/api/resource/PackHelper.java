package net.adequoir.muft.api.resource;

import net.adequoir.muft.api.Bootstrapper;

import java.util.function.Consumer;

public abstract class PackHelper {
    private static final PackHelper INSTANCE = Bootstrapper.instance().packHelper();

    protected abstract void register(PackStorage storage);

    public static void build(String namespace, Consumer<PackStorage> consumer) {
        var storage = new PackStorage(namespace);
        consumer.accept(storage);
        INSTANCE.register(storage);
    }

    protected PackHelper() {}
}