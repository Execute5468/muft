package net.adequoir.muft;

import net.adequoir.muft.api.client.network.ClientNetworkHelper;
import net.adequoir.muft.client.network.NeoForgeClientNetworkHelper;

public final class MuftClientBootstrapper {

    static {
        ClientNetworkHelper.bootstrap(new NeoForgeClientNetworkHelper());
    }

    public static void init() {}
    private MuftClientBootstrapper() {}
}
