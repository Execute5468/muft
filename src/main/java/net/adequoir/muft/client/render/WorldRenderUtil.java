package net.adequoir.muft.client.render;

import net.adequoir.muft.mixin.client.render.AxWorldRenderer;
import net.adequoir.muft.mixin.client.sound.AxSoundManager;
import net.adequoir.muft.mixin.client.sound.AxSoundSystem;
import net.adequoir.muft.mixutils.client.sound.ISource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import com.mojang.blaze3d.audio.Channel;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class WorldRenderUtil {
    private static final BiConsumer<BlockPos, SoundInstance> MUTING_BI_CONSUMER;
    private static final BiConsumer<BlockPos, SoundInstance> UNMUTING_BI_CONSUMER;

    public static void setAllJukeboxMute(boolean mute) {
        var worldRenderer = Minecraft.getInstance().levelRenderer;
        var playingSongs = ((AxWorldRenderer) worldRenderer).axPlayingSongs();

        var consumer = mute ? MUTING_BI_CONSUMER : UNMUTING_BI_CONSUMER;
        playingSongs.forEach(consumer);
    }

    static {
        Consumer<Channel> mutingConsumer = source -> ((ISource) source).muft_setMute(true);
        Consumer<Channel> unmutingConsumer = source -> ((ISource) source).muft_setMute(false);
        Function<Consumer<Channel>, BiConsumer<BlockPos, SoundInstance>> biConsumerProvider = sourceConsumer -> (pos, sound) -> {
            var soundManager = Minecraft.getInstance().getSoundManager();
            var soundSystem = ((AxSoundManager) soundManager).axSoundSystem();
            var sources = ((AxSoundSystem) soundSystem).axSources();

            var manager = sources.get(sound);
            if (manager != null) manager.execute(sourceConsumer);
        };

        MUTING_BI_CONSUMER = biConsumerProvider.apply(mutingConsumer);
        UNMUTING_BI_CONSUMER = biConsumerProvider.apply(unmutingConsumer);
    }

    private WorldRenderUtil() {}
}
