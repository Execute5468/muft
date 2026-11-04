package net.adequoir.muft.api.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public interface PlayerPayload extends CustomPacketPayload {
    void handle(Player player);
}
