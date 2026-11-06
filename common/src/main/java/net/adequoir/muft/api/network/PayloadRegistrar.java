package net.adequoir.muft.api.network;

import net.adequoir.muft.api.Bootstrapper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public abstract class PayloadRegistrar {
    private static final PayloadRegistrar INSTANCE = Bootstrapper.instance().payloadRegistrar();

    protected abstract <T extends PlayerPayload> void registerC2SPayload(CustomPacketPayload.Type<T> id, StreamCodec<RegistryFriendlyByteBuf, T> packetCodec);

    protected abstract <T extends PlayerPayload> void registerS2CPayload(CustomPacketPayload.Type<T> id, StreamCodec<RegistryFriendlyByteBuf, T> packetCodec);

    public static <T extends PlayerPayload> void registerC2S(
        CustomPacketPayload.Type<T> id,
        StreamMemberEncoder<? super RegistryFriendlyByteBuf, T> encoder,
        StreamDecoder<? super RegistryFriendlyByteBuf, T> decoder
    ) { INSTANCE.registerC2SPayload(id, new GenericPacketCodec<>(encoder, decoder)); }

    public static <T extends PlayerPayload> void registerS2C(
            CustomPacketPayload.Type<T> id,
            StreamMemberEncoder<? super RegistryFriendlyByteBuf, T> encoder,
            StreamDecoder<? super RegistryFriendlyByteBuf, T> decoder
    ) { INSTANCE.registerS2CPayload(id, new GenericPacketCodec<>(encoder, decoder)); }

    private record GenericPacketCodec<T extends PlayerPayload>(
            StreamMemberEncoder<? super RegistryFriendlyByteBuf, T> encoder,
            StreamDecoder<? super RegistryFriendlyByteBuf, T> decoder
    ) implements StreamCodec<RegistryFriendlyByteBuf, T> {
        public @Override T decode(RegistryFriendlyByteBuf buf) { return decoder.decode(buf); }
        public @Override void encode(RegistryFriendlyByteBuf buf, T value) { encoder.encode(value, buf); }
    }

    protected PayloadRegistrar() {}
}
