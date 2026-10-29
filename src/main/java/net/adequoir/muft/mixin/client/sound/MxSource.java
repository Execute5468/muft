package net.adequoir.muft.mixin.client.sound;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.mixutils.client.sound.ISource;
import net.minecraft.client.sound.Source;
import org.lwjgl.openal.AL10;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Source.class)
public class MxSource implements ISource {
    @Shadow private @Final int pointer;

    @Unique private boolean muted;
    @Unique private float cachedVolume;

    public @Override boolean muft_isMuted() { return muted; }
    public @Override void muft_setMute(boolean mute) { if (mute) mute(); else unmute(); }

    @Unique private void mute() { muted = true; AL10.alSourcef(pointer, 4106, 0.0F); }
    @Unique private void unmute() { muted = false; AL10.alSourcef(pointer, 4106, cachedVolume); }

    @Inject(method = "setVolume", at = @At("HEAD"), cancellable = true)
    private void onSetVolume(float volume, CallbackInfo ci) {
        cachedVolume = volume;
        if (!muted) ci.cancel();
    }
}
