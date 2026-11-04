package net.adequoir.muft.network;

import net.adequoir.muft.Muft;
import net.adequoir.muft.client.sound.PocketJukeboxManager;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.adequoir.muft.api.network.PlayerPayload;

public record PocketJukeboxPlayPayload(int slotId, int playbackId) implements PlayerPayload {
    public static final CustomPacketPayload.Type<PocketJukeboxPlayPayload> ID = new CustomPacketPayload.Type<>(
        Muft.id("pocket_jukebox_play")
    );

    public @Override Type<? extends CustomPacketPayload> type() { return ID; }

    public static PocketJukeboxPlayPayload read(FriendlyByteBuf buf) {
        return new PocketJukeboxPlayPayload(buf.readVarInt() - 1, buf.readVarInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(slotId + 1); // offset by 1 to make -1 positive and shave VarInt packet size
        buf.writeVarInt(playbackId);
    }

    public @Override void handle(Player player) {
        var inventory = player.getInventory();
        var stack = slotId < 0 ? inventory.offhand.getFirst() : inventory.items.get(slotId);
        PocketJukeboxManager.get((LocalPlayer) player).accept(stack, playbackId, player.isShiftKeyDown());
    }
}
