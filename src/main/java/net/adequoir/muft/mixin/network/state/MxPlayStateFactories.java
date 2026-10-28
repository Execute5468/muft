package net.adequoir.muft.mixin.network.state;

import net.adequoir.muft.network.packet.PocketJukeboxItemSelectedCSP;
import net.adequoir.muft.registry.MuftPackets;
import net.minecraft.network.NetworkStateBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.state.PlayStateFactories;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayStateFactories.class)
public class MxPlayStateFactories {

    @Inject(
        method = "method_55958",
        at = @At("TAIL")
    )
    private static void modC2S(NetworkStateBuilder<ServerPlayPacketListener, RegistryByteBuf> builder, CallbackInfo ci) {
        builder.add(MuftPackets.POCKET_JUKEBOX_ITEM_SELECTED, PocketJukeboxItemSelectedCSP.CODEC);
    }
}
