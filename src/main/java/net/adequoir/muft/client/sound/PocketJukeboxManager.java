package net.adequoir.muft.client.sound;

import net.adequoir.muft.client.render.WorldRenderUtil;
import net.adequoir.muft.client.util.PocketJukeboxPointer;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.mixin.client.sound.AxSoundManager;
import net.adequoir.muft.mixin.client.sound.AxSoundSystem;
import net.adequoir.muft.mixutils.client.sound.ISource;
import net.adequoir.muft.util.ItemEntry;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.*;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PocketJukeboxManager implements ClientPlayerTickable {
    private static final Consumer<Source> PAUSE_ACTION = source -> ((ISource) source).muft_setSoundPaused(true);
    private static final Consumer<Source> RESUME_ACTION = source -> ((ISource) source).muft_setSoundPaused(false);

    private final ClientPlayerEntity player;

    private @Nullable SoundInstance playingSong;
    private boolean isPlaying;

    private @Nullable PocketJukeboxPointer pointer;

    public PocketJukeboxManager(ClientPlayerEntity player) { this.player = player; }

    public boolean isPlaying() { return isPlaying; }

    public void loadPointer(long prevId, PocketJukeboxPointer ptr) {
        if (this.pointer == null || pointer.id() != prevId) {
            if (!isPlaying) WorldRenderUtil.setAllJukeboxMute(true);
            startPlaying(ptr.component());
            isPlaying = true;
        } else {
            togglePause();
        }
        pointer = ptr;
    }

    private void togglePause() {
        isPlaying = !isPlaying;
        WorldRenderUtil.setAllJukeboxMute(isPlaying);

        var srcManager = sourceManager();
        if (srcManager != null) srcManager.run(isPlaying ? RESUME_ACTION : PAUSE_ACTION);
    }

    private void stopPlaying() {
        if (!isPlaying) return;
        isPlaying = false;
        WorldRenderUtil.setAllJukeboxMute(false);
        closeSong();
    }

    private void startPlaying(PocketJukeboxComponent component) {
        ItemEntry entry;
        if (component.isEmpty() || (entry = component.get(0)).isEmpty()) return;

        closeSong();
        var jukeboxSongEntry = getSongEntry(entry);
        if (jukeboxSongEntry == null) return;
        var jukeboxSong = jukeboxSongEntry.value();
        var soundEvent = jukeboxSong.soundEvent().value();
        playingSong = createSoundInstance(soundEvent);
        soundManager().play(playingSong);
        client().inGameHud.setRecordPlayingOverlay(jukeboxSong.description());
    }

    private void closeSong() {
        if (playingSong == null) return;
        soundManager().stop(playingSong);
        playingSong = null;
    }

    public @Override void tick() {
        if (pointer == null) return;
        var newPtr = pointer.update(player);
        if (newPtr == pointer) return;
        pointer = newPtr;
        if (pointer == null) stopPlaying();
    }

    private PositionedSoundInstance createSoundInstance(SoundEvent sound) {
        return new PositionedSoundInstance(
            sound.getId(),
            SoundCategory.RECORDS,
            4.0f, 1.0F,
            SoundInstance.createRandom(),
            false, 0,
            SoundInstance.AttenuationType.NONE,
            0.0, 0.0, 0.0,
            true
        );
    }

    @SuppressWarnings("OptionalIsPresent")
    private @Nullable RegistryEntry<JukeboxSong> getSongEntry(ItemEntry entry) {
        var jukeboxComponent = entry.get(DataComponentTypes.JUKEBOX_PLAYABLE);
        if (jukeboxComponent == null) return null;
        var registryPair = jukeboxComponent.song();

        var songEntry = registryPair.entry();
        var songKey = registryPair.key();
        if (songEntry.isPresent()) return songEntry.get();

        return wrapperLookup().getWrapperOrThrow(songKey.getRegistryRef()).getOptional(songKey).orElse(null);
    }

    private static MinecraftClient client() { return MinecraftClient.getInstance();  }
    private RegistryWrapper.WrapperLookup wrapperLookup() { return player.clientWorld.getRegistryManager(); }
    private SoundManager soundManager() { return MinecraftClient.getInstance().getSoundManager(); }
    private @Nullable Channel.SourceManager sourceManager() {
        if (playingSong == null) return null;
        return ((AxSoundSystem) ((AxSoundManager) soundManager()).axSoundSystem()).axSources().get(playingSong);
    }
}
