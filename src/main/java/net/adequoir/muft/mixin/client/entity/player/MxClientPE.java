package net.adequoir.muft.mixin.client.entity.player;

import net.adequoir.muft.client.sound.PocketJukeboxManager;
import net.adequoir.muft.mixutils.client.entity.player.IClientPE;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientPlayerEntity.class)
public class MxClientPE implements IClientPE {
    @Unique private final PocketJukeboxManager pocketJukeboxManager = new PocketJukeboxManager();
    public @Override PocketJukeboxManager muft_pocketJukeboxManager() { return pocketJukeboxManager; }
}