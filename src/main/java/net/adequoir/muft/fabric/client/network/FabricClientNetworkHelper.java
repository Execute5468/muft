package net.adequoir.muft.fabric.client.network;

import net.adequoir.muft.api.client.network.ClientNetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class FabricClientNetworkHelper extends ClientNetworkHelper {
    protected @Override void sendPayload(CustomPacketPayload payload) { ClientPlayNetworking.send(payload); }
}
