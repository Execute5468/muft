package net.adequoir.muft.fabric;

import net.adequoir.muft.api.LoaderUtil;
import net.fabricmc.loader.api.FabricLoader;

public class FabricLoaderUtil extends LoaderUtil {
    protected @Override boolean hasModId(String modId) { return FabricLoader.getInstance().isModLoaded(modId); }
}
