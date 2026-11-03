package net.adequoir.muft.network;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.client.sound.PocketJukeboxManager;
import net.adequoir.muft.network.api.PlayerPayload;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;

public record PocketJukeboxPlayPayload(int slotId, int playbackId) implements PlayerPayload {
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

    public @Override void handle(PlayerEntity player) {
        var inventory = player.getInventory();
        var stack = slotId < 0 ? inventory.offHand.getFirst() : inventory.main.get(slotId);
        PocketJukeboxManager.get((ClientPlayerEntity) player).accept(stack, playbackId, player.isSneaking());
    }
}
