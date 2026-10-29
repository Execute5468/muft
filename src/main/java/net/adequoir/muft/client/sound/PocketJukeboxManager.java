package net.adequoir.muft.client.sound;

import net.adequoir.muft.client.render.WorldRenderUtil;
import net.adequoir.muft.client.util.PocketJukeboxPointer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PocketJukeboxManager implements ClientPlayerTickable {
    private final ClientPlayerEntity player;
    private boolean isPlaying;

    private @Nullable PocketJukeboxPointer pointer;

    public PocketJukeboxManager(ClientPlayerEntity player) { this.player = player; }

    public boolean isPlaying() { return isPlaying; }
    public void togglePlaying(ItemStack stack, int discIndex) {
        Objects.requireNonNull(stack);
        if (pointer == null || !pointer.test(stack)) pointer = PocketJukeboxPointer.fromStack(player, stack, discIndex);

        if (pointer != null) {
            isPlaying = !isPlaying;
            WorldRenderUtil.setAllJukeboxMute(isPlaying);
        } else stopPlaying();
    }

    private void stopPlaying() {
        if (!isPlaying) return;
        isPlaying = false;
        WorldRenderUtil.setAllJukeboxMute(false);
    }

    public @Override void tick() {
        if (pointer == null) return;
        var newPointer = pointer.update(player);
        if (newPointer == pointer) return;
        pointer = newPointer;
        if (pointer == null) stopPlaying();
    }
}
