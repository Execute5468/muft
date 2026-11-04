package net.adequoir.muft.api.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public abstract class ServerNetworkHelper {
    private static @Nullable ServerNetworkHelper instance;

    protected abstract void sendPayload(ServerPlayer player, CustomPacketPayload payload);

    public static void send(ServerPlayer player, CustomPacketPayload payload) {
        if (instance != null) { instance.sendPayload(player, payload); return; }
        throw new IllegalStateException("ServerNetworkHelper has not been bootstrapped!");
    }

    public static void bootstrap(ServerNetworkHelper helper) {
        if (instance == null) { instance = helper; return; }
        throw new IllegalStateException("ServerNetworkHelper already bootstrapped!");
    }

    protected ServerNetworkHelper() {}
}
