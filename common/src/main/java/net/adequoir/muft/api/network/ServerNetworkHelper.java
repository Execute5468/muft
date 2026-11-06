package net.adequoir.muft.api.network;

import net.adequoir.muft.api.Bootstrapper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public abstract class ServerNetworkHelper {
    private static final ServerNetworkHelper INSTANCE = Bootstrapper.instance().serverNetworkHelper();

    protected abstract void sendPayload(ServerPlayer player, CustomPacketPayload payload);

    public static void send(ServerPlayer player, CustomPacketPayload payload) { INSTANCE.sendPayload(player, payload); }

    protected ServerNetworkHelper() {}
}
