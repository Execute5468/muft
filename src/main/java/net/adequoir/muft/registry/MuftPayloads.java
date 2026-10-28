package net.adequoir.muft.registry;

import net.adequoir.muft.network.PocketJukeboxItemSelectedPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.ValueFirstEncoder;
import net.minecraft.network.packet.CustomPayload;

public final class MuftPayloads {
    static {
        registerC2S(PocketJukeboxItemSelectedPayload.ID, PocketJukeboxItemSelectedPayload::write, PocketJukeboxItemSelectedPayload::read);
        registerServerReceiver(PocketJukeboxItemSelectedPayload.ID, PocketJukeboxItemSelectedPayload::handle);
    }

    private static <T extends CustomPayload> void registerServerReceiver(
        CustomPayload.Id<T> id,
        ServerPlayNetworking.PlayPayloadHandler<T> handler
    ) {
        ServerPlayNetworking.registerGlobalReceiver(id, handler);
    }

    private static <T extends CustomPayload> void registerC2S(
        CustomPayload.Id<T> id,
        ValueFirstEncoder<? super RegistryByteBuf, T> encoder,
        PacketDecoder<? super RegistryByteBuf, T> decoder
    ) { PayloadTypeRegistry.playC2S().register(id, new GenericPacketCodec<>(encoder, decoder)); }

    private record GenericPacketCodec<T extends CustomPayload>(
        ValueFirstEncoder<? super RegistryByteBuf, T> encoder,
        PacketDecoder<? super RegistryByteBuf, T> decoder
    ) implements PacketCodec<RegistryByteBuf, T> {
        public @Override T decode(RegistryByteBuf buf) { return decoder.decode(buf); }
        public @Override void encode(RegistryByteBuf buf, T value) { encoder.encode(value, buf); }
    }

    public static void init() {}

    private MuftPayloads() {}
}
