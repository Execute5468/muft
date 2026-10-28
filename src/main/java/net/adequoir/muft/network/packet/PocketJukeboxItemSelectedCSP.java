package net.adequoir.muft.network.packet;

import net.adequoir.muft.mixutils.screen.MuftSHandler;
import net.adequoir.muft.registry.MuftPackets;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public record PocketJukeboxItemSelectedCSP(int slotId, int selectedIndex) implements Packet<ServerPlayPacketListener> {
    public static final PacketCodec<PacketByteBuf, PocketJukeboxItemSelectedCSP> CODEC = Packet.createCodec(
        PocketJukeboxItemSelectedCSP::write, PocketJukeboxItemSelectedCSP::new
    );

    private PocketJukeboxItemSelectedCSP(PacketByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt());
        if (selectedIndex < -1) throw new IllegalArgumentException("Invalid selectedIndex: " + selectedIndex);
    }

    private void write(PacketByteBuf buf) {
        buf.writeVarInt(slotId);
        buf.writeVarInt(selectedIndex);
    }

    public @Override PacketType<? extends Packet<ServerPlayPacketListener>> getPacketId() {
        return MuftPackets.POCKET_JUKEBOX_ITEM_SELECTED;
    }

    public @Override void apply(ServerPlayPacketListener listener) {
        if (!(listener instanceof ServerPlayNetworkHandler networkHandler)) return;
        ((MuftSHandler) networkHandler.player.currentScreenHandler).muft_selectPocketJukeboxStack(slotId, selectedIndex);
    }
}
