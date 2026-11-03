package net.adequoir.muft;

import net.adequoir.muft.api.client.network.ClientNetworkHelper;
import net.adequoir.muft.fabric.client.network.FabricClientNetworkHelper;

public final class MuftClientBootstrapper {
    static {
        ClientNetworkHelper.bootstrap(new FabricClientNetworkHelper());
    }

    public static void init() {}

    private MuftClientBootstrapper() {}
}
