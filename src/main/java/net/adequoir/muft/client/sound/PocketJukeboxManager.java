package net.adequoir.muft.client.sound;

import net.adequoir.muft.client.render.WorldRenderUtil;

public class PocketJukeboxManager {
    private static final int BUFFER_SIZE = 16;
    private static final int BUFFER_WRAP_MASK = BUFFER_SIZE - 1;

    private boolean isPlaying;

    public boolean isPlaying() { return isPlaying; }
    public void togglePlaying() {
        isPlaying = !isPlaying;
        WorldRenderUtil.setAllJukeboxMute(isPlaying);
    }
}
