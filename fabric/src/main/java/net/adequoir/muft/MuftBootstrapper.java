package net.adequoir.muft;

import net.adequoir.muft.api.LoaderUtil;
import net.adequoir.muft.api.resource.PackHelper;
import net.adequoir.muft.item.FabricCreativeMenuEntries;
import net.adequoir.muft.network.FabricPayloadRegistrar;
import net.adequoir.muft.network.FabricServerNetworkHelper;
import net.adequoir.muft.registry.FabricRegistrar;
import net.adequoir.muft.api.item.CreativeMenuEntries;
import net.adequoir.muft.api.network.PayloadRegistrar;
import net.adequoir.muft.api.network.ServerNetworkHelper;
import net.adequoir.muft.api.registry.Registrars;
import net.adequoir.muft.resource.FabricPackHelper;

public final class MuftBootstrapper {
    static {
        LoaderUtil.bootstrap(new FabricLoaderUtil());
        Registrars.bootstrap(FabricRegistrar.FACTORY);
        ServerNetworkHelper.bootstrap(new FabricServerNetworkHelper());
        PayloadRegistrar.bootstrap(new FabricPayloadRegistrar());
        CreativeMenuEntries.bootstrap(new FabricCreativeMenuEntries());
        PackHelper.bootstrap(new FabricPackHelper());
    }

    public static void init() {}

    private MuftBootstrapper() {}
}
