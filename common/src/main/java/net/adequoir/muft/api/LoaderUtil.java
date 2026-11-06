package net.adequoir.muft.api;

public abstract class LoaderUtil {
    private static final LoaderUtil INSTANCE = Bootstrapper.instance().loaderUtil();

    protected abstract boolean hasModId(String modId);
    protected abstract Platform getPlatform();
    protected abstract boolean isClientInstance();

    public static boolean isLoaded(String modId) { return INSTANCE.hasModId(modId); }
    public static boolean isClient() { return INSTANCE.isClientInstance(); }
    public static Platform platform() { return INSTANCE.getPlatform(); }
}