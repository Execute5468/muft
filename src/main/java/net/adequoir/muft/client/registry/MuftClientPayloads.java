package net.adequoir.muft.client.registry;

import net.adequoir.muft.network.PocketJukeboxPlayPayload;
import net.adequoir.muft.util.FabricDependant;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.packet.CustomPayload;

@FabricDependant
public final class MuftClientPayloads {

    static {
        registerClientReceiver(PocketJukeboxPlayPayload.ID, (payload, context) -> payload.handle(context.player()));
    }

    private static <T extends CustomPayload> void registerClientReceiver(
        CustomPayload.Id<T> id,
        ClientPlayNetworking.PlayPayloadHandler<T> handler
    ) { ClientPlayNetworking.registerGlobalReceiver(id, handler); }

    public static void init() {}

    private MuftClientPayloads() {}
}
