package net.adequoir.muft.mixin.client.entity.player;

import net.adequoir.muft.client.sound.PocketJukeboxManager;
import net.adequoir.muft.mixutils.client.entity.player.IClientPE;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.stats.StatsCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LocalPlayer.class)
public class MxClientPE implements IClientPE {
    @Shadow private @Final List<AmbientSoundHandler> ambientSoundHandlers;

    @Unique private final PocketJukeboxManager pocketJukeboxManager = new PocketJukeboxManager((LocalPlayer) (Object) this);

    public @Override PocketJukeboxManager muft_pocketJukeboxTicker() { return pocketJukeboxManager; }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(
        Minecraft client,
        ClientLevel world,
        ClientPacketListener networkHandler,
        StatsCounter stats,
        ClientRecipeBook recipeBook,
        boolean lastSneaking,
        boolean lastSprinting,
        CallbackInfo ci
    ) { ambientSoundHandlers.add(pocketJukeboxManager); }
}