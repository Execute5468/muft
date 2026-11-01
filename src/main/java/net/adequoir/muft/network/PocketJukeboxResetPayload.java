package net.adequoir.muft.network;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.item.PocketJukeboxItem;
import net.adequoir.muft.mixin.client.entity.player.AxPlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public record PocketJukeboxResetPayload(int invType, int slotId) implements CustomPayload {
    public static final int T_CURSOR_STACK = -2;

    public static final CustomPayload.Id<PocketJukeboxResetPayload> ID = new CustomPayload.Id<>(
            MusicalFeature.id("pocket_jukebox_reset")
    );

    public @Override Id<? extends CustomPayload> getId() { return ID; }

    public static PocketJukeboxResetPayload read(PacketByteBuf buf) {
        return new PocketJukeboxResetPayload(buf.readVarInt(), buf.readVarInt());
    }
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(invType);
        buf.writeVarInt(slotId);
    }

    public void handle(ServerPlayerEntity player) {
        var stack = invType == T_CURSOR_STACK
                ? player.currentScreenHandler.getCursorStack()
                : ((AxPlayerInventory) player.getInventory()).axCombinedInventory().get(invType).get(slotId);

        if (stack.isEmpty() || !(stack.getItem() instanceof PocketJukeboxItem)) return;
        var component = PocketJukeboxComponent.getOrNull(stack);
        if (component == null) return;
        var newComponent = component.withPlaying(0);
        PocketJukeboxComponent.set(stack, newComponent);
    }
}
