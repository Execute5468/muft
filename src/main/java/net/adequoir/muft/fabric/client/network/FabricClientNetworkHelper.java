package net.adequoir.muft.fabric.client.network;

import net.adequoir.muft.client.network.ClientNetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.packet.CustomPayload;

public class FabricClientNetworkHelper extends ClientNetworkHelper {
    protected @Override void sendPayload(CustomPayload payload) { ClientPlayNetworking.send(payload); }
}
