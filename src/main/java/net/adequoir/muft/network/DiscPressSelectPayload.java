package net.adequoir.muft.network;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.api.network.PlayerPayload;
import net.adequoir.muft.screen.DiscPressScreenHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public record DiscPressSelectPayload(int index) implements PlayerPayload {
    public static final CustomPacketPayload.Type<DiscPressSelectPayload> ID = new CustomPacketPayload.Type<>(
        MusicalFeature.id("disc_press_selected")
    );

    public @Override Type<DiscPressSelectPayload> type() { return ID; }

    public static DiscPressSelectPayload read(FriendlyByteBuf buf) {
        return new DiscPressSelectPayload(buf.readVarInt());
    }
    public void write(FriendlyByteBuf buf) { buf.writeVarInt(index); }

    public @Override void handle(Player player) {
        if (!(player.containerMenu instanceof DiscPressScreenHandler screenHandler)) return;
        screenHandler.setSelectedIndex(index);
    }
}
