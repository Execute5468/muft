package net.adequoir.muft;

import net.adequoir.muft.api.LoaderUtil;
import net.adequoir.muft.api.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class FabricLoaderUtil extends LoaderUtil {
    protected @Override boolean hasModId(String modId) { return FabricLoader.getInstance().isModLoaded(modId); }
    protected @Override boolean isClientInstance() { return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT; }

    protected @Override Platform getPlatform() { return Platform.FABRIC; }
}
