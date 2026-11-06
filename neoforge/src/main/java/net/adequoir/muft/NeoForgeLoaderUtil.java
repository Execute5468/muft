package net.adequoir.muft;

import net.adequoir.muft.api.LoaderUtil;
import net.adequoir.muft.api.Platform;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class NeoForgeLoaderUtil extends LoaderUtil {
    protected @Override boolean hasModId(String modId) { return ModList.get().isLoaded(modId); }
    protected @Override Platform getPlatform() { return Platform.NEOFORGE; }
    protected @Override boolean isClientInstance() { return FMLEnvironment.dist == Dist.CLIENT; }
}