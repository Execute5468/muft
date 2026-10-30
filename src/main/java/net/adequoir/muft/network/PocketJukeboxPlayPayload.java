package net.adequoir.muft.network;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.client.util.PocketJukeboxPointer;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.mixutils.client.entity.player.IClientPE;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;

public record PocketJukeboxPlayPayload(int slotId, long playbackId) implements CustomPayload {
    public static final CustomPayload.Id<PocketJukeboxPlayPayload> ID = new CustomPayload.Id<>(
        MusicalFeature.id("pocket_jukebox_play")
    );

    public @Override Id<? extends CustomPayload> getId() { return ID; }

    public static PocketJukeboxPlayPayload read(PacketByteBuf buf) {
        return new PocketJukeboxPlayPayload(buf.readVarInt() - 1, buf.readVarLong());
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(slotId + 1); // offset by 1 to make -1 positive and shave VarInt packet size
        buf.writeVarLong(playbackId);
    }

    @Environment(EnvType.CLIENT)
    public void handle(ClientPlayerEntity player) {
        var inventory = player.getInventory();
        var stack = slotId < 0 ? inventory.offHand.getFirst() : inventory.main.get(slotId);
        var component = PocketJukeboxComponent.getOrNull(stack);
        if (component == null) return;
        var newComponent = component.withPlaying(playbackId);
        PocketJukeboxComponent.set(stack, newComponent);

        var invType = slotId < 0 ? 2 : 0;
        var slotId = Math.max(this.slotId, 0);
        var ptr = new PocketJukeboxPointer(stack.getItem(), newComponent, invType, slotId);
        ((IClientPE) player).muft_pocketJukeboxManager().loadPointer(component.playbackId(), ptr);
    }
}
