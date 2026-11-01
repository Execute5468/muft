package net.adequoir.muft.network;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.mixutils.screen.MuftSHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public record PocketJukeboxItemSelectedPayload(int slotId, int selectedIndex) implements CustomPayload {
    public static final CustomPayload.Id<PocketJukeboxItemSelectedPayload> ID = new CustomPayload.Id<>(
        MusicalFeature.id("pocket_jukebox_item_selected")
    );

    public @Override Id<? extends CustomPayload> getId() { return ID; }

    public static PocketJukeboxItemSelectedPayload read(PacketByteBuf buf) {
        return new PocketJukeboxItemSelectedPayload(buf.readVarInt(), buf.readVarInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(slotId);
        buf.writeVarInt(selectedIndex);
    }

    public void handle(ServerPlayerEntity player) {
        ((MuftSHandler) player.currentScreenHandler).muft_selectPocketJukeboxStack(slotId, selectedIndex);
    }
}
