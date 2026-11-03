package net.adequoir.muft.network.api;

import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public abstract class ServerNetworkHelper {
    private static @Nullable ServerNetworkHelper instance;

    protected abstract void sendPayload(ServerPlayerEntity player, CustomPayload payload);

    public static void send(ServerPlayerEntity player, CustomPayload payload) {
        if (instance != null) { instance.sendPayload(player, payload); return; }
        throw new IllegalStateException("ServerNetworkHelper has not been bootstrapped!");
    }

    public static void bootstrap(ServerNetworkHelper helper) {
        if (instance == null) { instance = helper; return; }
        throw new IllegalStateException("ServerNetworkHelper already bootstrapped!");
    }

    protected ServerNetworkHelper() {}
}
