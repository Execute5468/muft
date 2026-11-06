package net.adequoir.muft.api.client.network;

import net.adequoir.muft.api.client.ClientBootstrapper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;

public abstract class ClientNetworkHelper {
    private static final ClientNetworkHelper INSTANCE = ClientBootstrapper.instance().clientNetworkHelper();

    protected abstract void sendPayload(CustomPacketPayload payload);

    public static void send(CustomPacketPayload payload) { INSTANCE.sendPayload(payload); }

    protected ClientNetworkHelper() {}
}