package net.adequoir.muft.api;

public abstract class LoaderUtil {
    private static final LoaderUtil INSTANCE = Bootstrapper.instance().loaderUtil();

    protected abstract boolean hasModId(String modId);
    protected abstract Platform getPlatform();

    public static boolean isLoaded(String modId) { return INSTANCE.hasModId(modId); }

    public static Platform platform() { return INSTANCE.getPlatform(); }
}