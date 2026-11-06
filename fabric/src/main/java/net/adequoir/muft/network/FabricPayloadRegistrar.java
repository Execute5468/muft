package net.adequoir.muft.network;

import net.adequoir.muft.Muft;
import net.adequoir.muft.api.network.PayloadRegistrar;
import net.adequoir.muft.api.network.PlayerPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class FabricPayloadRegistrar extends PayloadRegistrar {
    protected @Override <T extends PlayerPayload> void registerC2SPayload(CustomPacketPayload.Type<T> id, StreamCodec<RegistryFriendlyByteBuf, T> packetCodec) {
        PayloadTypeRegistry.playC2S().register(id, packetCodec);
        ServerPlayNetworking.registerGlobalReceiver(id, (payload, context) -> payload.handle(context.player()));
    }

    protected @Override <T extends PlayerPayload> void registerS2CPayload(CustomPacketPayload.Type<T> id, StreamCodec<RegistryFriendlyByteBuf, T> packetCodec) {
        PayloadTypeRegistry.playS2C().register(id, packetCodec);
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return;
        Muft.LOGGER.info("Registering client receiver for packet {}", id);
        ClientUtil.registerClient(id);
    }

    private static class ClientUtil {
        @Environment(EnvType.CLIENT)
        private static  <T extends PlayerPayload> void registerClient(CustomPacketPayload.Type<T> id) {
            ClientPlayNetworking.registerGlobalReceiver(id, (payload, context) -> payload.handle(context.player()));
        }
    }
}
