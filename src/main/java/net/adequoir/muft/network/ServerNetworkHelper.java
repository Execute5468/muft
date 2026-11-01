package net.adequoir.muft.network;

import net.adequoir.muft.util.FabricDependant;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

@FabricDependant
public final class ServerNetworkHelper {
    public static void send(ServerPlayerEntity player, CustomPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    private ServerNetworkHelper() {}
}
