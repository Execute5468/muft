package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.network.packet.PocketJukeboxItemSelectedCSP;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public final class MuftPackets {
    public static final PacketType<PocketJukeboxItemSelectedCSP> POCKET_JUKEBOX_ITEM_SELECTED = registerC2S("pocket_jukebox_item_selected");

    private static <T extends Packet<ServerPlayPacketListener>> PacketType<T> registerS2C(String id) {
        return new PacketType<>(NetworkSide.CLIENTBOUND , MusicalFeature.id(id));
    }

    private static <T extends Packet<ServerPlayPacketListener>> PacketType<T> registerC2S(String id) {
        return new PacketType<>(NetworkSide.SERVERBOUND, MusicalFeature.id(id));
    }

    private MuftPackets() {}
}
