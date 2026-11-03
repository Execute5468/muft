package net.adequoir.muft.network;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.network.api.PlayerPayload;
import net.adequoir.muft.screen.DiscPressScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public record DiscPressSelectPayload(int index) implements PlayerPayload {
    public static final CustomPayload.Id<DiscPressSelectPayload> ID = new CustomPayload.Id<>(
        MusicalFeature.id("disc_press_selected")
    );

    public @Override Id<DiscPressSelectPayload> getId() { return ID; }

    public static DiscPressSelectPayload read(PacketByteBuf buf) {
        return new DiscPressSelectPayload(buf.readVarInt());
    }
    public void write(PacketByteBuf buf) { buf.writeVarInt(index); }

    public @Override void handle(PlayerEntity player) {
        if (!(player.currentScreenHandler instanceof DiscPressScreenHandler screenHandler)) return;
        screenHandler.setSelectedIndex(index);
    }
}
