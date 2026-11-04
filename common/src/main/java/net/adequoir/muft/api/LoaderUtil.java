package net.adequoir.muft.api;

import org.jetbrains.annotations.Nullable;

public abstract class LoaderUtil {
    private static @Nullable LoaderUtil instance;

    protected abstract boolean hasModId(String modId);

    public static boolean isLoaded(String modId) { return instance().hasModId(modId); }

    public static void bootstrap(LoaderUtil util) {
        if (instance == null) { instance = util; return; }
        throw new IllegalStateException("LoaderUtil already bootstrapped!");
    }

    private static LoaderUtil instance() {
        if (instance != null) return instance;
        throw new IllegalStateException("LoaderUtil has not been bootstrapped!");
    }
}
