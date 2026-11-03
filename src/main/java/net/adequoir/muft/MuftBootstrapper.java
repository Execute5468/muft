package net.adequoir.muft;

import net.adequoir.muft.api.LoaderUtil;
import net.adequoir.muft.api.resource.PackHelper;
import net.adequoir.muft.fabric.FabricLoaderUtil;
import net.adequoir.muft.fabric.item.FabricCreativeMenuEntries;
import net.adequoir.muft.fabric.network.FabricPayloadRegistrar;
import net.adequoir.muft.fabric.network.FabricServerNetworkHelper;
import net.adequoir.muft.fabric.registry.FabricRegistrar;
import net.adequoir.muft.api.item.CreativeMenuEntries;
import net.adequoir.muft.api.network.PayloadRegistrar;
import net.adequoir.muft.api.network.ServerNetworkHelper;
import net.adequoir.muft.api.registry.Registrars;
import net.adequoir.muft.fabric.resource.FabricPackHelper;

public final class MuftBootstrapper {
    static {
        Registrars.bootstrap(FabricRegistrar.FACTORY);
        ServerNetworkHelper.bootstrap(new FabricServerNetworkHelper());
        PayloadRegistrar.bootstrap(new FabricPayloadRegistrar());
        CreativeMenuEntries.bootstrap(new FabricCreativeMenuEntries());
        PackHelper.bootstrap(new FabricPackHelper());
        LoaderUtil.bootstrap(new FabricLoaderUtil());
    }

    public static void init() {}

    private MuftBootstrapper() {}
}
