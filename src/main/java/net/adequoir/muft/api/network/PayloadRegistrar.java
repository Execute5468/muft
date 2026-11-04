package net.adequoir.muft.api.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;

public abstract class PayloadRegistrar {
    private static @Nullable PayloadRegistrar instance;

    protected abstract <T extends PlayerPayload> void registerC2SPayload(CustomPacketPayload.Type<T> id, StreamCodec<RegistryFriendlyByteBuf, T> packetCodec);

    protected abstract <T extends PlayerPayload> void registerS2CPayload(CustomPacketPayload.Type<T> id, StreamCodec<RegistryFriendlyByteBuf, T> packetCodec);

    public static <T extends PlayerPayload> void registerC2S(
        CustomPacketPayload.Type<T> id,
        StreamMemberEncoder<? super RegistryFriendlyByteBuf, T> encoder,
        StreamDecoder<? super RegistryFriendlyByteBuf, T> decoder
    ) {
        if (instance != null) { instance.registerC2SPayload(id, new GenericPacketCodec<>(encoder, decoder)); return; }
        throwNotBootstrapped();
    }

    public static <T extends PlayerPayload> void registerS2C(
            CustomPacketPayload.Type<T> id,
            StreamMemberEncoder<? super RegistryFriendlyByteBuf, T> encoder,
            StreamDecoder<? super RegistryFriendlyByteBuf, T> decoder
    ) {
        if (instance != null) { instance.registerS2CPayload(id, new GenericPacketCodec<>(encoder, decoder)); return; }
        throwNotBootstrapped();
    }

    public static void bootstrap(PayloadRegistrar registrar) {
        if (instance == null) { instance = registrar; return; }
        throw new IllegalStateException("PayloadRegistrar already bootstrapped!");
    }

    private record GenericPacketCodec<T extends PlayerPayload>(
            StreamMemberEncoder<? super RegistryFriendlyByteBuf, T> encoder,
            StreamDecoder<? super RegistryFriendlyByteBuf, T> decoder
    ) implements StreamCodec<RegistryFriendlyByteBuf, T> {
        public @Override T decode(RegistryFriendlyByteBuf buf) { return decoder.decode(buf); }
        public @Override void encode(RegistryFriendlyByteBuf buf, T value) { encoder.encode(value, buf); }
    }

    private static void throwNotBootstrapped() { throw new IllegalStateException("PayloadRegistrar has not been bootstrapped!"); }

    protected PayloadRegistrar() {}
}
