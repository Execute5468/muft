package net.adequoir.muft.network;

import net.adequoir.muft.Muft;
import net.adequoir.muft.api.network.PayloadRegistrar;
import net.adequoir.muft.api.network.PlayerPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.ArrayDeque;
import java.util.ArrayList;

@NotNullByDefault
@EventBusSubscriber(modid = Muft.MOD_ID)
public class NeoForgePayloadRegistrar extends PayloadRegistrar {
    private static final ArrayDeque<Entry<?>> payloads = new ArrayDeque<>();

    protected @Override <T extends PlayerPayload> void registerC2SPayload(
        CustomPacketPayload.Type<T> id,
        StreamCodec<RegistryFriendlyByteBuf, T> packetCodec
    ) { payloads.add(new EntryC2S<>(id, packetCodec)); }

    protected @Override <T extends PlayerPayload> void registerS2CPayload(
        CustomPacketPayload.Type<T> id,
        StreamCodec<RegistryFriendlyByteBuf, T> packetCodec
    ) { payloads.add(new EntryS2C<>(id, packetCodec)); }

    @SubscribeEvent
    public static void initialize(final RegisterPayloadHandlersEvent event) {
        if (payloads.isEmpty()) return;
        Muft.LOGGER.info("Registering payloads");
        final var registrar = event.registrar("1");
        var payloads = NeoForgePayloadRegistrar.payloads;
        while (!payloads.isEmpty()) payloads.pop().register(registrar);
    }

    private interface Entry<T extends PlayerPayload> {
        void register(net.neoforged.neoforge.network.registration.PayloadRegistrar registrar);
    }

    private record EntryC2S<T extends PlayerPayload>(
        CustomPacketPayload.Type<T> id,
        StreamCodec<RegistryFriendlyByteBuf, T> packetCodec
    ) implements Entry<T> {
        public @Override void register(net.neoforged.neoforge.network.registration.PayloadRegistrar registrar) {
            Muft.LOGGER.info("Registering C2S payload {}", id);
            registrar.playToServer(
                id,
                packetCodec,
                (payload, context) -> payload.handle(context.player())
            );
        }
    }

    private record EntryS2C<T extends PlayerPayload>(
        CustomPacketPayload.Type<T> id,
        StreamCodec<RegistryFriendlyByteBuf, T> packetCodec
    ) implements Entry<T> {
        public @Override void register(net.neoforged.neoforge.network.registration.PayloadRegistrar registrar) {
            Muft.LOGGER.info("Registering S2C payload {}", id);
            registrar.playToClient(
                id,
                packetCodec,
                (payload, context) -> payload.handle(context.player())
            );
        }
    }
}
