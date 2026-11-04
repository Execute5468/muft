package net.adequoir.muft.network;

import net.adequoir.muft.Muft;
import net.adequoir.muft.mixutils.screen.MuftSHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.adequoir.muft.api.network.PlayerPayload;

public record PocketJukeboxItemSelectedPayload(int slotId, int selectedIndex) implements PlayerPayload {
    public static final CustomPacketPayload.Type<PocketJukeboxItemSelectedPayload> ID = new CustomPacketPayload.Type<>(
        Muft.id("pocket_jukebox_item_selected")
    );

    public @Override Type<? extends CustomPacketPayload> type() { return ID; }

    public static PocketJukeboxItemSelectedPayload read(FriendlyByteBuf buf) {
        return new PocketJukeboxItemSelectedPayload(buf.readVarInt(), buf.readVarInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(slotId);
        buf.writeVarInt(selectedIndex);
    }

    public @Override void handle(Player player) {
        ((MuftSHandler) player.containerMenu).muft_selectPocketJukeboxStack(slotId, selectedIndex);
    }
}
