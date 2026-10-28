package net.adequoir.muft.network;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.mixutils.screen.MuftSHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;

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

    public static void handle(
        PocketJukeboxItemSelectedPayload payload,
        ServerPlayNetworking.Context context
    ) { ((MuftSHandler) context.player().currentScreenHandler).muft_selectPocketJukeboxStack(payload.slotId, payload.selectedIndex); }
}
