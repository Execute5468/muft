package net.adequoir.muft.api.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;

public interface PlayerPayload extends CustomPayload {
    void handle(PlayerEntity player);
}
