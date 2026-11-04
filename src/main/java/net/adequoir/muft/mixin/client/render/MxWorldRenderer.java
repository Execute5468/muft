package net.adequoir.muft.mixin.client.render;

import com.llamalad7.mixinextras.sugar.Local;
import net.adequoir.muft.client.sound.PocketJukeboxManager;
import net.adequoir.muft.mixin.client.sound.AxSoundManager;
import net.adequoir.muft.mixin.client.sound.AxSoundSystem;
import net.adequoir.muft.mixutils.client.sound.ISource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.item.JukeboxSong;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MxWorldRenderer {
    @Shadow private @Final Minecraft minecraft;

    @Inject(
        method = "playJukeboxSong",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;setNowPlaying(Lnet/minecraft/network/chat/Component;)V"
        )
    )
    private void onPlayJukeboxSong(Holder<JukeboxSong> song, BlockPos jukeboxPos, CallbackInfo ci, @Local SoundInstance instance) {
        tryMuteJukeboxSong(instance);
    }

    @Unique private void tryMuteJukeboxSong(SoundInstance instance) {
        var clientPlayer = minecraft.player;
        if (clientPlayer == null) return;
        var isPlaying = PocketJukeboxManager.get(clientPlayer).isPlaying();
        if (!isPlaying) return;
        var soundSystem = (AxSoundSystem) ((AxSoundManager) minecraft.getSoundManager()).axSoundSystem();
        var sources = soundSystem.axSources();
        var manager = sources.get(instance);
        if (manager != null) manager.execute(source -> ((ISource) source).muft_setMute(true));
    }
}
