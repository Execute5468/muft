package net.adequoir.muft.mixin.client.render;

import com.llamalad7.mixinextras.sugar.Local;
import net.adequoir.muft.client.sound.PocketJukeboxTicker;
import net.adequoir.muft.mixin.client.sound.AxSoundManager;
import net.adequoir.muft.mixin.client.sound.AxSoundSystem;
import net.adequoir.muft.mixutils.client.entity.player.IClientPE;
import net.adequoir.muft.mixutils.client.sound.ISource;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MxWorldRenderer {
    @Shadow private @Final MinecraftClient client;

    @Inject(
        method = "playJukeboxSong",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;setRecordPlayingOverlay(Lnet/minecraft/text/Text;)V"
        )
    )
    private void onPlayJukeboxSong(RegistryEntry<JukeboxSong> song, BlockPos jukeboxPos, CallbackInfo ci, @Local SoundInstance instance) {
        tryMuteJukeboxSong(instance);
    }

    @Unique private void tryMuteJukeboxSong(SoundInstance instance) {
        var clientPlayer = client.player;
        if (clientPlayer == null) return;
        var isPlaying = PocketJukeboxTicker.get(clientPlayer).isPlaying();
        if (!isPlaying) return;
        var soundSystem = (AxSoundSystem) ((AxSoundManager) client.getSoundManager()).axSoundSystem();
        var sources = soundSystem.axSources();
        var manager = sources.get(instance);
        if (manager != null) manager.run(source -> ((ISource) source).muft_setMute(true));
    }
}
