package net.adequoir.muft.client.network;

import net.adequoir.muft.util.FabricDependant;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.packet.CustomPayload;

@FabricDependant
public final class ClientNetworkHelper {
    public static void send(CustomPayload payload) { ClientPlayNetworking.send(payload); }

    private ClientNetworkHelper() {}
}
