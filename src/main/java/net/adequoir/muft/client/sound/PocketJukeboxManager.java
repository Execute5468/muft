package net.adequoir.muft.client.sound;

import net.adequoir.muft.client.render.WorldRenderUtil;
import net.adequoir.muft.client.util.PocketJukeboxPointer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import org.jetbrains.annotations.Nullable;

public class PocketJukeboxManager implements ClientPlayerTickable {
    private final ClientPlayerEntity player;
    private boolean isPlaying;

    private @Nullable PocketJukeboxPointer pointer;

    public PocketJukeboxManager(ClientPlayerEntity player) { this.player = player; }

    public boolean isPlaying() { return isPlaying; }

    public void loadPointer(long prevId, PocketJukeboxPointer ptr) {
        if (this.pointer == null || pointer.id() != prevId) {
            if (!isPlaying) WorldRenderUtil.setAllJukeboxMute(true);
            isPlaying = true;
        } else {
            isPlaying = !isPlaying;
            WorldRenderUtil.setAllJukeboxMute(isPlaying);
        }
        pointer = ptr;
    }

    private void stopPlaying() {
        if (!isPlaying) return;
        isPlaying = false;
        WorldRenderUtil.setAllJukeboxMute(false);
    }

    public @Override void tick() {
        if (pointer == null) return;
        var newPtr = pointer.update(player);
        if (newPtr == pointer) return;
        pointer = newPtr;
        if (pointer == null) stopPlaying();
    }
}
