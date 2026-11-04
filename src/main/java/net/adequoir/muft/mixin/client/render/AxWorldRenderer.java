package net.adequoir.muft.mixin.client.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;

@Mixin(LevelRenderer.class)
public interface AxWorldRenderer {
    @Accessor("playingJukeboxSongs")
    Map<BlockPos, SoundInstance> axPlayingSongs();
}
