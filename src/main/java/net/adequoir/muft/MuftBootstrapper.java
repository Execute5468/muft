package net.adequoir.muft;

import net.adequoir.muft.fabric.item.FabricCreativeMenuEntries;
import net.adequoir.muft.fabric.network.FabricPayloadRegistrar;
import net.adequoir.muft.fabric.network.FabricServerNetworkHelper;
import net.adequoir.muft.fabric.registry.api.FabricRegistrar;
import net.adequoir.muft.item.api.CreativeMenuEntries;
import net.adequoir.muft.network.api.PayloadRegistrar;
import net.adequoir.muft.network.api.ServerNetworkHelper;
import net.adequoir.muft.registry.api.Registrars;

public final class MuftBootstrapper {
    static {
        Registrars.bootstrap(FabricRegistrar.FACTORY);
        ServerNetworkHelper.bootstrap(new FabricServerNetworkHelper());
        PayloadRegistrar.bootstrap(new FabricPayloadRegistrar());
        CreativeMenuEntries.bootstrap(new FabricCreativeMenuEntries());
    }

    public static void init() {}

    private MuftBootstrapper() {}
}
