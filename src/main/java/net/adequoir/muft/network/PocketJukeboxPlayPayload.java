package net.adequoir.muft.network;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.client.sound.PocketJukeboxManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;

public record PocketJukeboxPlayPayload(int slotId, int playbackId) implements CustomPayload {
    public static final CustomPayload.Id<PocketJukeboxPlayPayload> ID = new CustomPayload.Id<>(
        MusicalFeature.id("pocket_jukebox_play")
    );

    public @Override Id<? extends CustomPayload> getId() { return ID; }

    public static PocketJukeboxPlayPayload read(PacketByteBuf buf) {
        return new PocketJukeboxPlayPayload(buf.readVarInt() - 1, buf.readVarInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(slotId + 1); // offset by 1 to make -1 positive and shave VarInt packet size
        buf.writeVarInt(playbackId);
    }

    @Environment(EnvType.CLIENT)
    public void handle(ClientPlayerEntity player) {
        var inventory = player.getInventory();
        var stack = slotId < 0 ? inventory.offHand.getFirst() : inventory.main.get(slotId);
        PocketJukeboxManager.get(player).accept(stack, playbackId);
    }
}
