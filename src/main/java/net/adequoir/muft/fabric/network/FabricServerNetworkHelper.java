package net.adequoir.muft.fabric.network;

import net.adequoir.muft.api.network.ServerNetworkHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class FabricServerNetworkHelper extends ServerNetworkHelper {
    protected @Override void sendPayload(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }
}
