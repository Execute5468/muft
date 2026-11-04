package net.adequoir.muft.network;

import net.adequoir.muft.Muft;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.item.PocketJukeboxItem;
import net.adequoir.muft.mixin.client.entity.player.AxPlayerInventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.adequoir.muft.api.network.PlayerPayload;

public record PocketJukeboxResetPayload(int invType, int slotId) implements PlayerPayload {
    public static final int T_CURSOR_STACK = -2;

    public static final CustomPacketPayload.Type<PocketJukeboxResetPayload> ID = new CustomPacketPayload.Type<>(
            Muft.id("pocket_jukebox_reset")
    );

    public @Override Type<? extends CustomPacketPayload> type() { return ID; }

    public static PocketJukeboxResetPayload read(FriendlyByteBuf buf) {
        return new PocketJukeboxResetPayload(buf.readVarInt(), buf.readVarInt());
    }
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(invType);
        buf.writeVarInt(slotId);
    }

    public @Override void handle(Player player) {
        var stack = invType == T_CURSOR_STACK
                ? player.containerMenu.getCarried()
                : ((AxPlayerInventory) player.getInventory()).axCombinedInventory().get(invType).get(slotId);

        if (stack.isEmpty() || !(stack.getItem() instanceof PocketJukeboxItem)) return;
        var component = PocketJukeboxComponent.getOrNull(stack);
        if (component == null) return;
        var newComponent = component.withPlaying(0);
        PocketJukeboxComponent.set(stack, newComponent);
    }
}
