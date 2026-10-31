package net.adequoir.muft.mixin.client.sound;

import net.adequoir.muft.client.util.SoundState;
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
public abstract class MxSource implements ISource {
    @Shadow private @Final int pointer;

    @Unique private long soundState = 0L;

    @Shadow protected abstract int getSourceState();

    public @Override boolean muft_isMuted() { return SoundState.isMuted(soundState); }
    public @Override void muft_setMute(boolean mute) { if (mute) mute(); else unmute(); }

    @Unique private void mute() {
        soundState = SoundState.withMuted(soundState, true);
        AL10.alSourcef(pointer, 4106, 0.0F);
    }
    @Unique private void unmute() {
        soundState = SoundState.withMuted(soundState, false);
        AL10.alSourcef(pointer, 4106, SoundState.getCachedVolume(soundState));
    }

    @Inject(method = "setVolume", at = @At("HEAD"), cancellable = true)
    private void onSetVolume(float volume, CallbackInfo ci) {
        soundState = SoundState.withCachedVolume(soundState, volume);
        if (!SoundState.isMuted(soundState)) ci.cancel();
    }

    public @Override boolean muft_isSoundPaused() { return SoundState.isSoundPaused(soundState); }

    public @Override void muft_setSoundPaused(boolean paused) {
        soundState = SoundState.withSoundPaused(soundState, paused);
        if (SoundState.isGamePaused(soundState)) return;

        int sourceState = getSourceState();

        if (paused && sourceState == 4114) {
            AL10.alSourcePause(pointer);
        } else if (!paused && sourceState == 4115) {
            AL10.alSourcePlay(pointer);
        }
    }

    @Inject(method = "pause", at = @At("HEAD"), cancellable = true)
    private void onPause(CallbackInfo ci) {
        boolean anyPaused = SoundState.isAnyPaused(soundState);
        soundState = SoundState.withGamePaused(soundState, true);
        if (anyPaused) ci.cancel(); // cancel if already paused
    }

    @Inject(method = "resume", at = @At("HEAD"), cancellable = true)
    private void onResume(CallbackInfo ci) {
        soundState = SoundState.withGamePaused(soundState, false);
        if (SoundState.isSoundPaused(soundState)) ci.cancel(); // cancel if sound itself is paused
    }
}
