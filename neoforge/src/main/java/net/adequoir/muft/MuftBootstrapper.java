package net.adequoir.muft;

import net.adequoir.muft.api.LoaderUtil;
import net.adequoir.muft.api.item.CreativeMenuEntries;
import net.adequoir.muft.api.network.PayloadRegistrar;
import net.adequoir.muft.api.network.ServerNetworkHelper;
import net.adequoir.muft.api.registry.Registrars;
import net.adequoir.muft.api.resource.PackHelper;
import net.adequoir.muft.item.NeoForgeCreativeMenuEntries;
import net.adequoir.muft.network.NeoForgePayloadRegistrar;
import net.adequoir.muft.network.NeoForgeServerNetworkHelper;
import net.adequoir.muft.registry.NeoForgeRegistrar;
import net.adequoir.muft.resource.NeoForgePackHelper;
import net.neoforged.bus.api.IEventBus;

public final class MuftBootstrapper {
    private static boolean hasInit = false;

    public static void init(IEventBus eventBus) {
        if (hasInit) return;
        hasInit = true;

        LoaderUtil.bootstrap(new NeoForgeLoaderUtil());
        Registrars.bootstrap(NeoForgeRegistrar.FACTORY_FUNCTION.apply(eventBus));
        ServerNetworkHelper.bootstrap(new NeoForgeServerNetworkHelper());
        PayloadRegistrar.bootstrap(new NeoForgePayloadRegistrar());
        CreativeMenuEntries.bootstrap(new NeoForgeCreativeMenuEntries());
        PackHelper.bootstrap(new NeoForgePackHelper());
    }
    private MuftBootstrapper() {}
}
