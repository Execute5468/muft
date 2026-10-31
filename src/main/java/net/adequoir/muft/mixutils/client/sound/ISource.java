package net.adequoir.muft.mixutils.client.sound;

public interface ISource {
    boolean muft_isMuted();
    void muft_setMute(boolean mute);

    boolean muft_isSoundPaused();
    void muft_setSoundPaused(boolean paused);
}
