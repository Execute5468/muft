package net.adequoir.muft.client.sound;

import com.mojang.blaze3d.audio.Channel;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.adequoir.muft.client.render.WorldRenderUtil;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.item.PocketJukeboxItem;
import net.adequoir.muft.mixin.client.entity.player.AxPlayerInventory;
import net.adequoir.muft.mixin.client.sound.AxSoundManager;
import net.adequoir.muft.mixin.client.sound.AxSoundSystem;
import net.adequoir.muft.mixutils.client.entity.player.IClientPE;
import net.adequoir.muft.mixutils.client.sound.ISource;
import net.adequoir.muft.api.client.network.ClientNetworkHelper;
import net.adequoir.muft.network.PocketJukeboxResetPayload;
import net.adequoir.muft.util.ItemEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.*;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public final class PocketJukeboxManager implements AmbientSoundHandler {
    public static final int T_CURSOR_STACK = PocketJukeboxResetPayload.T_CURSOR_STACK;
    private static final Consumer<Channel> PAUSE_ACTION = source -> ((ISource) source).muft_setSoundPaused(true);
    private static final Consumer<Channel> RESUME_ACTION = source -> ((ISource) source).muft_setSoundPaused(false);
    private static final Component SONG_PAUSED = Component.translatable("pocket_jukebox.music_paused");

    private final LocalPlayer player;

    private final RandomSource random = RandomSource.createNewThreadLocalInstance();
    private final IntOpenHashSet tickingPlayIds = new IntOpenHashSet();
    private final Int2ObjectOpenHashMap<SongEntry> playingSongs = new Int2ObjectOpenHashMap<>();
    private int playingId = 0;

    public PocketJukeboxManager(LocalPlayer player) { this.player = player; }

    public static PocketJukeboxManager get(LocalPlayer player) { return ((IClientPE) player).muft_pocketJukeboxTicker(); }

    public static void onRemove(
        Player player,
        ItemStack stack, int invType, int invSlot,
        PocketJukeboxComponent component, int discIndex
    ) { get((LocalPlayer) player).onRemove(stack, invType, invSlot, component, discIndex); }

    public static void onRemove(Player player, ItemStack stack, Slot slot, PocketJukeboxComponent component, int discIndex) {
        if (!(slot.container instanceof Inventory)) return;
        var combinedInv = ((AxPlayerInventory) player.getInventory()).axCombinedInventory();

        var invSlot = slot.getContainerSlot();
        for (int i = 0; i < combinedInv.size(); i++) {
            var invSize = combinedInv.get(i).size();
            if (invSlot >= invSize) { invSlot -= invSize; continue; }
            onRemove(player, stack, i, invSlot, component, discIndex);
            return;
        }
    }

    public boolean isPlaying() { return playingId != 0; }

    public int playingId() {  return playingId; }

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

    @SuppressWarnings("ConstantValue")
    public int getDiscId(PocketJukeboxComponent component) {
        if (component.playbackId() == 0) return -1;
        var entry = playingSongs.get(component.playbackId());
        return entry == null ? -1 : entry.discIndex;
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
        if (srcManager != null) srcManager.execute(PAUSE_ACTION);
    }

    private void pauseAndUpdateCurrentSong(int newPlaybackId) {
        if (playingId == 0) return;
        var playId = playingId;
        playingId = 0;
        var entry = playingSongs.remove(playId);
        var srcManager = sourceManagerOf(entry);
        if (srcManager == null) return;
        srcManager.execute(PAUSE_ACTION);
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
                srcManager.execute(RESUME_ACTION);
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
        tickStack(player.containerMenu.getCarried(), T_CURSOR_STACK, -1);
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
        ClientNetworkHelper.send(new PocketJukeboxResetPayload(invType, slotId));
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

    private static void playDescription(Holder<JukeboxSong> entry) { client().gui.setNowPlaying(entry.value().description()); }
    private static void playPaused() {
        client().gui.setOverlayMessage(PocketJukeboxManager.SONG_PAUSED, true);
        client().getNarrator().sayNow(PocketJukeboxManager.SONG_PAUSED);
    }
    private static Minecraft client() { return Minecraft.getInstance();  }
    private static SoundManager soundManager() { return Minecraft.getInstance().getSoundManager(); }
    private static @Nullable ChannelAccess.ChannelHandle sourceManagerOf(@Nullable SongEntry songEntry) {
        if (songEntry == null) return null;
        return ((AxSoundSystem) ((AxSoundManager) soundManager()).axSoundSystem()).axSources().get(songEntry.soundInstance);
    }

    private static class SongEntry {
        int ticks;
        int discIndex;
        final Holder<JukeboxSong> songEntry;
        final SoundInstance soundInstance;

        public SongEntry(LocalPlayer player, PocketJukeboxComponent component, int discIndex) {
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

        boolean playing() { return !songEntry.value().hasFinished(ticks); }

        void stop() { soundManager().stop(soundInstance); }

        private static SimpleSoundInstance createSoundInstance(SoundEvent sound) {
            return new SimpleSoundInstance(
                sound.getLocation(),
                SoundSource.RECORDS,
                4.0f, 1.0F,
                SoundInstance.createUnseededRandom(),
                false, 0,
                SoundInstance.Attenuation.NONE,
                0.0, 0.0, 0.0,
                true
            );
        }

        @SuppressWarnings("OptionalIsPresent")
        private static @Nullable Holder<JukeboxSong> getSongEntry(LocalPlayer player, ItemEntry entry) {
            var jukeboxComponent = entry.get(DataComponents.JUKEBOX_PLAYABLE);
            if (jukeboxComponent == null) return null;
            var registryPair = jukeboxComponent.song();

            var songEntry = registryPair.holder();
            var songKey = registryPair.key();
            if (songEntry.isPresent()) return songEntry.get();

            return wrapperLookup(player).lookupOrThrow(songKey.registryKey()).get(songKey).orElse(null);
        }

        private static HolderLookup.Provider wrapperLookup(LocalPlayer player) {
            return player.clientLevel.registryAccess();
        }
    }
}
