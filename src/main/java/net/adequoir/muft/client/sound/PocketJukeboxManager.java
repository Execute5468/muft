package net.adequoir.muft.client.sound;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.adequoir.muft.client.render.WorldRenderUtil;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.item.PocketJukeboxItem;
import net.adequoir.muft.mixin.client.sound.AxSoundManager;
import net.adequoir.muft.mixin.client.sound.AxSoundSystem;
import net.adequoir.muft.mixutils.client.entity.player.IClientPE;
import net.adequoir.muft.mixutils.client.sound.ISource;
import net.adequoir.muft.network.PocketJukeboxResetPayload;
import net.adequoir.muft.util.ItemEntry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.*;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public final class PocketJukeboxManager implements ClientPlayerTickable {
    public static final int T_CURSOR_STACK = PocketJukeboxResetPayload.T_CURSOR_STACK;
    private static final Consumer<Source> PAUSE_ACTION = source -> ((ISource) source).muft_setSoundPaused(true);
    private static final Consumer<Source> RESUME_ACTION = source -> ((ISource) source).muft_setSoundPaused(false);
    private static final Text SONG_PAUSED = Text.translatable("pocket_jukebox.music_paused");

    private final ClientPlayerEntity player;

    private final Random random = Random.createLocal();
    private final IntOpenHashSet tickingPlayIds = new IntOpenHashSet();
    private final Int2ObjectOpenHashMap<SongEntry> playingSongs = new Int2ObjectOpenHashMap<>();
    private int playingId = 0;

    public PocketJukeboxManager(ClientPlayerEntity player) { this.player = player; }

    public static PocketJukeboxManager get(ClientPlayerEntity player) { return ((IClientPE) player).muft_pocketJukeboxTicker(); }

    public boolean isPlaying() { return playingId != 0; }

    public void onRemove(ItemStack stack, int invType, int invSlot, PocketJukeboxComponent component, int discIndex) {
        var playbackId = component.playbackId();
        var songEntry = playingSongs.get(playbackId);
        //noinspection ConstantValue
        if (songEntry == null) return;
        if (songEntry.discIndex > discIndex) {
            songEntry.discIndex--;
        } else if (songEntry.discIndex == discIndex) {
            resetStack(stack, component, invType, invSlot);
            songEntry.stop();
            playingSongs.remove(playbackId);
            if (playingId == playbackId) {
                playingId = 0;
                WorldRenderUtil.setAllJukeboxMute(false);
            }
        }
    }

    public void accept(ItemStack stack, int newPlaybackId, boolean sneaking) {
        var component = PocketJukeboxComponent.getOrNull(stack);
        if (component == null) return;
        var newComponent = component.withPlaying(newPlaybackId);
        PocketJukeboxComponent.set(stack, newComponent);

        if (playingId == 0) {
            WorldRenderUtil.setAllJukeboxMute(true);
            playNewSong(component, newPlaybackId, sneaking);
        } else if (component.playbackId() == playingId) {
            if (sneaking) {
                skipAndUpdateCurrentSong(component, newPlaybackId);
            } else pauseAndUpdateCurrentSong(newPlaybackId);
        } else {
            pauseCurrentSong();
            playNewSong(component, newPlaybackId, sneaking);
        }
    }

    private void pauseCurrentSong() {
        if (playingId == 0) return;
        var playId = playingId;
        playingId = 0;
        var entry = playingSongs.get(playId);
        var srcManager = sourceManagerOf(entry);
        if (srcManager != null) srcManager.run(PAUSE_ACTION);
    }

    private void pauseAndUpdateCurrentSong(int newPlaybackId) {
        if (playingId == 0) return;
        var playId = playingId;
        playingId = 0;
        var entry = playingSongs.remove(playId);
        var srcManager = sourceManagerOf(entry);
        if (srcManager == null) return;
        srcManager.run(PAUSE_ACTION);
        WorldRenderUtil.setAllJukeboxMute(false);
        playingSongs.put(newPlaybackId, entry);
        playPaused();
    }

    private void skipAndUpdateCurrentSong(PocketJukeboxComponent component, int newPlaybackId) {
        var entry = playingSongs.remove(playingId);
        //noinspection ConstantValue
        if (entry != null) entry.stop();
        var nextSong = pickRandom(component, entry.discIndex);
        var songEntry = new SongEntry(player, component, nextSong);
        soundManager().play(songEntry.soundInstance);
        playDescription(songEntry.songEntry);
        playingSongs.put(newPlaybackId, songEntry);
        playingId = newPlaybackId;
    }

    @SuppressWarnings("ConstantValue")
    private void playNewSong(PocketJukeboxComponent component, int newPlaybackId, boolean sneaking) {
        int prevId = component.playbackId();
        var prevEntry = playingSongs.remove(prevId);
        SongEntry songEntry;


        if (sneaking && prevEntry != null) {
            prevEntry.stop();
            var randomSong = pickRandom(component, prevEntry.discIndex);
            songEntry = new SongEntry(player, component, randomSong);
            soundManager().play(songEntry.soundInstance);
        } else if (prevId == 0 || prevEntry == null) {
            var randomSong = pickRandom(component, -1);
            songEntry = new SongEntry(player, component, randomSong);
            soundManager().play(songEntry.soundInstance);
        } else {
            var srcManager = sourceManagerOf(prevEntry);
            if (srcManager != null) {
                srcManager.run(RESUME_ACTION);
            } else soundManager().play(prevEntry.soundInstance);
            songEntry = prevEntry;
        }

        playDescription(songEntry.songEntry);
        playingSongs.put(newPlaybackId, songEntry);
        playingId = newPlaybackId;
    }

    private int pickRandom(PocketJukeboxComponent component, int lastSong) {
        if (component.size() == 1) return 0;
        if (lastSong < 0) return random.nextInt(component.size());
        var randomInt = random.nextInt(component.size() - 1);
        return randomInt >= lastSong ? randomInt + 1 : randomInt;
    }

    public @Override void tick() {
        tickStack(player.currentScreenHandler.getCursorStack(), T_CURSOR_STACK, -1);
        tickSongs();
    }

    public void tickStack(ItemStack stack, int invType, int slotId) {
        if (stack.isEmpty() || !((stack.getItem()) instanceof PocketJukeboxItem)) return;
        var component = PocketJukeboxComponent.getOrNull(stack);
        if (component == null) return;
        var playbackId = component.playbackId();
        if (playbackId == 0) return;

        if (!playingSongs.containsKey(playbackId) || !tickingPlayIds.add(playbackId)) {
            resetStack(stack, component, invType, slotId);
        } else if (playbackId == playingId) tickCurrentSong(component);
    }

    private void resetStack(ItemStack stack, PocketJukeboxComponent component, int invType, int slotId) {
        ClientPlayNetworking.send(new PocketJukeboxResetPayload(invType, slotId));
        var newComponent = component.withPlaying(0);
        PocketJukeboxComponent.set(stack, newComponent);
    }

    private void tickSongs() {
        var tickingPlayIds = this.tickingPlayIds;
        var playingSongs = this.playingSongs;

        if (!playingSongs.isEmpty()) {
            var it = playingSongs.int2ObjectEntrySet().fastIterator();
            while (it.hasNext()) {
                var entry = it.next();
                var playbackId = entry.getIntKey();
                if (tickingPlayIds.contains(playbackId)) continue;
                if (playbackId == playingId) {
                    playingId = 0;
                    WorldRenderUtil.setAllJukeboxMute(false);
                }
                entry.getValue().stop();
                it.remove();
            }
        }
        tickingPlayIds.clear();
    }

    private void tickCurrentSong(PocketJukeboxComponent component) {
        var songEntry = playingSongs.get(playingId);
        //noinspection ConstantValue
        if (songEntry == null) { playingId = 0; return; }
        songEntry.ticks++;
        if (songEntry.playing()) return;
        songEntry.stop();
        var nextDiscIndex = pickRandom(component, songEntry.discIndex);
        var nextSongEntry = new SongEntry(player, component, nextDiscIndex);
        soundManager().play(nextSongEntry.soundInstance);
        playDescription(nextSongEntry.songEntry);
        playingSongs.put(playingId, nextSongEntry);
    }

    private static void playDescription(RegistryEntry<JukeboxSong> entry) { client().inGameHud.setRecordPlayingOverlay(entry.value().description()); }
    private static void playPaused() {
        client().inGameHud.setOverlayMessage(PocketJukeboxManager.SONG_PAUSED, true);
        client().getNarratorManager().narrate(PocketJukeboxManager.SONG_PAUSED);
    }
    private static MinecraftClient client() { return MinecraftClient.getInstance();  }
    private static SoundManager soundManager() { return MinecraftClient.getInstance().getSoundManager(); }
    private static @Nullable Channel.SourceManager sourceManagerOf(@Nullable SongEntry songEntry) {
        if (songEntry == null) return null;
        return ((AxSoundSystem) ((AxSoundManager) soundManager()).axSoundSystem()).axSources().get(songEntry.soundInstance);
    }

    private static class SongEntry {
        int ticks;
        int discIndex;
        final RegistryEntry<JukeboxSong> songEntry;
        final SoundInstance soundInstance;

        public SongEntry(ClientPlayerEntity player, PocketJukeboxComponent component, int discIndex) {
            var songEntry = getSongEntry(player, component.get(discIndex));
            Objects.requireNonNull(songEntry);

            var jukeboxSong = songEntry.value();
            var soundEvent = jukeboxSong.soundEvent().value();
            var soundInstance = createSoundInstance(soundEvent);

            this.ticks = 0;
            this.discIndex = discIndex;
            this.songEntry = songEntry;
            this.soundInstance = soundInstance;
        }

        boolean playing() { return !songEntry.value().shouldStopPlaying(ticks); }

        void stop() { soundManager().stop(soundInstance); }

        private static PositionedSoundInstance createSoundInstance(SoundEvent sound) {
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
        private static @Nullable RegistryEntry<JukeboxSong> getSongEntry(ClientPlayerEntity player, ItemEntry entry) {
            var jukeboxComponent = entry.get(DataComponentTypes.JUKEBOX_PLAYABLE);
            if (jukeboxComponent == null) return null;
            var registryPair = jukeboxComponent.song();

            var songEntry = registryPair.entry();
            var songKey = registryPair.key();
            if (songEntry.isPresent()) return songEntry.get();

            return wrapperLookup(player).getWrapperOrThrow(songKey.getRegistryRef()).getOptional(songKey).orElse(null);
        }

        private static RegistryWrapper.WrapperLookup wrapperLookup(ClientPlayerEntity player) {
            return player.clientWorld.getRegistryManager();
        }
    }
}
