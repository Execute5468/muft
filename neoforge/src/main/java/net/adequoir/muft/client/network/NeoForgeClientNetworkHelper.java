package net.adequoir.muft.client.network;

import net.adequoir.muft.api.client.network.ClientNetworkHelper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class NeoForgeClientNetworkHelper extends ClientNetworkHelper {
    protected @Override void sendPayload(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }
}
