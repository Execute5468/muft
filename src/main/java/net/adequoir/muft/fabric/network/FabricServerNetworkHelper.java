package net.adequoir.muft.fabric.network;

import net.adequoir.muft.network.api.ServerNetworkHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public class FabricServerNetworkHelper extends ServerNetworkHelper {
    protected @Override void sendPayload(ServerPlayerEntity player, CustomPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }
}
