package net.adequoir.muft.api.client;

import net.adequoir.muft.api.client.network.ClientNetworkHelper;
import net.adequoir.muft.client.network.NeoForgeClientNetworkHelper;

public class ClientBootstrapperImpl extends ClientBootstrapper {
    public @Override ClientNetworkHelper clientNetworkHelper() { return new NeoForgeClientNetworkHelper(); }
}
