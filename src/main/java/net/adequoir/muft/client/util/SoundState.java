package net.adequoir.muft.client.util;

public final class SoundState {
    private static final long VOLUME = -1L >>> (Long.SIZE - Float.SIZE);
    private static final long MUTE = 1L << Float.SIZE;
    private static final long GAME_PAUSE = MUTE << 1;
    private static final long SOUND_PAUSE = GAME_PAUSE << 1;
    private static final long ANY_PAUSE = GAME_PAUSE | SOUND_PAUSE;

    public static float getCachedVolume(long state) { return Float.intBitsToFloat((int) state); }
    public static long withCachedVolume(long state, float volume) {
        return (state & ~VOLUME) | ((long) Float.floatToIntBits(volume)) & VOLUME;
    }

    public static boolean isMuted(long state) { return (state & MUTE) != 0L; }
    public static long withMuted(long state, boolean muted) { return muted ? state | MUTE : state & ~MUTE; }

    public static boolean isAnyPaused(long state) { return (state & ANY_PAUSE) != 0L; }

    public static boolean isGamePaused(long state) { return (state & GAME_PAUSE) != 0L; }
    public static long withGamePaused(long state, boolean paused) { return paused ? state | GAME_PAUSE : state & ~GAME_PAUSE; }

    public static boolean isSoundPaused(long state) { return (state & SOUND_PAUSE) != 0L; }
    public static long withSoundPaused(long state, boolean paused) { return paused ? state | SOUND_PAUSE : state & ~SOUND_PAUSE; }

    private SoundState() {}
}