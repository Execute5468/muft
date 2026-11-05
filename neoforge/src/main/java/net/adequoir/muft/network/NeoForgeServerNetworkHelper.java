package net.adequoir.muft.network;

import net.adequoir.muft.api.network.ServerNetworkHelper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class NeoForgeServerNetworkHelper extends ServerNetworkHelper {
    protected @Override void sendPayload(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }
}
