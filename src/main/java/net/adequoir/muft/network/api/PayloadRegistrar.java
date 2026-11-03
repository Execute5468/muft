package net.adequoir.muft.network.api;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.ValueFirstEncoder;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.Nullable;

public abstract class PayloadRegistrar {
    private static @Nullable PayloadRegistrar instance;

    protected abstract <T extends PlayerPayload> void registerC2SPayload(CustomPayload.Id<T> id, PacketCodec<RegistryByteBuf, T> packetCodec);

    protected abstract <T extends PlayerPayload> void registerS2CPayload(CustomPayload.Id<T> id, PacketCodec<RegistryByteBuf, T> packetCodec);

    public static <T extends PlayerPayload> void registerC2S(
        CustomPayload.Id<T> id,
        ValueFirstEncoder<? super RegistryByteBuf, T> encoder,
        PacketDecoder<? super RegistryByteBuf, T> decoder
    ) {
        if (instance != null) { instance.registerC2SPayload(id, new GenericPacketCodec<>(encoder, decoder)); return; }
        throwNotBootstrapped();
    }

    public static <T extends PlayerPayload> void registerS2C(
            CustomPayload.Id<T> id,
            ValueFirstEncoder<? super RegistryByteBuf, T> encoder,
            PacketDecoder<? super RegistryByteBuf, T> decoder
    ) {
        if (instance != null) { instance.registerS2CPayload(id, new GenericPacketCodec<>(encoder, decoder)); return; }
        throwNotBootstrapped();
    }

    public static void bootstrap(PayloadRegistrar registrar) {
        if (instance == null) { instance = registrar; return; }
        throw new IllegalStateException("PayloadRegistrar already bootstrapped!");
    }

    private record GenericPacketCodec<T extends PlayerPayload>(
            ValueFirstEncoder<? super RegistryByteBuf, T> encoder,
            PacketDecoder<? super RegistryByteBuf, T> decoder
    ) implements PacketCodec<RegistryByteBuf, T> {
        public @Override T decode(RegistryByteBuf buf) { return decoder.decode(buf); }
        public @Override void encode(RegistryByteBuf buf, T value) { encoder.encode(value, buf); }
    }

    private static void throwNotBootstrapped() { throw new IllegalStateException("PayloadRegistrar has not been bootstrapped!"); }

    protected PayloadRegistrar() {}
}
