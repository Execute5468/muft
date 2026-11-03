package net.adequoir.muft.fabric.network;

import net.adequoir.muft.api.network.PayloadRegistrar;
import net.adequoir.muft.api.network.PlayerPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class FabricPayloadRegistrar extends PayloadRegistrar {
    protected @Override <T extends PlayerPayload> void registerC2SPayload(CustomPayload.Id<T> id, PacketCodec<RegistryByteBuf, T> packetCodec) {
        PayloadTypeRegistry.playC2S().register(id, packetCodec);
        ServerPlayNetworking.registerGlobalReceiver(id, (payload, context) -> payload.handle(context.player()));
    }

    protected @Override <T extends PlayerPayload> void registerS2CPayload(CustomPayload.Id<T> id, PacketCodec<RegistryByteBuf, T> packetCodec) {
        PayloadTypeRegistry.playS2C().register(id, packetCodec);
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return;
        ClientPlayNetworking.registerGlobalReceiver(id, (payload, context) -> payload.handle(context.player()));
    }
}
