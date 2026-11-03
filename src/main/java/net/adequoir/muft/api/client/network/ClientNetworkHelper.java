package net.adequoir.muft.api.client.network;

import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.Nullable;

public abstract class ClientNetworkHelper {
    private static @Nullable ClientNetworkHelper instance;

    protected abstract void sendPayload(CustomPayload payload);

    public static void send(CustomPayload payload) {
        if (instance != null) { instance.sendPayload(payload); return; }
        throw new IllegalStateException("ClientNetworkHelper has not been bootstrapped!");
    }

    public static void bootstrap(ClientNetworkHelper helper) {
        if (instance == null) { instance = helper; return; }
        throw new IllegalStateException("ClientNetworkHelper already bootstrapped!");
    }

    protected ClientNetworkHelper() {}
}