package net.adequoir.muft.client.gui.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.client.config.MuftConfigManager;
import net.adequoir.muft.client.sound.PocketJukeboxManager;
import net.adequoir.muft.client.util.RenderHelper;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class PocketJukeboxTooltipComponent implements MuftTooltipComponent {
    private static final int MAX_SLOT_COUNT = PocketJukeboxComponent.MAX_SLOT_COUNT;

    private static final int SLOTS_PER_ROW = PocketJukeboxComponent.MAX_ROW_LENGTH;
    private static final int SLOT_DIMENSION = 24;
    private static final int ROW_WIDTH = SLOTS_PER_ROW * SLOT_DIMENSION;

    private static final int PROGRESS_BORDER_WIDTH = ROW_WIDTH;
    private static final int PROGRESS_BORDER_HEIGHT = 13;
    private static final int PROGRESS_BORDER_MARGIN = 1;
    private static final int PROGRESS_BAR_WIDTH = PROGRESS_BORDER_WIDTH - (2 * PROGRESS_BORDER_MARGIN);
    private static final int PROGRESS_BAR_HEIGHT = PROGRESS_BORDER_HEIGHT - (2 * PROGRESS_BORDER_MARGIN);
    private static final int Y_MARGIN = 4;

    private static final ResourceLocation PROGRESS_BORDER_TEXTURE = MusicalFeature.id("container/pocket_jukebox/pocket_jukebox_progressbar_border");
    private static final ResourceLocation PROGRESS_FILL_TEXTURE = MusicalFeature.id("container/pocket_jukebox/pocket_jukebox_progressbar_fill");
    private static final ResourceLocation PROGRESS_FULL_TEXTURE = MusicalFeature.id("container/pocket_jukebox/pocket_jukebox_progressbar_full");
    private static final ResourceLocation SLOT_HIGHLIGHT_BACK_TEXTURE = MusicalFeature.id("container/pocket_jukebox/slot_highlight_back");
    private static final ResourceLocation SLOT_HIGHLIGHT_FRONT_TEXTURE = MusicalFeature.id("container/pocket_jukebox/slot_highlight_front");
    private static final ResourceLocation SLOT_BACKGROUND_TEXTURE = MusicalFeature.id("container/pocket_jukebox/slot_background");
    private static final ResourceLocation SLOT_PLAYING_TEXTURE = MusicalFeature.id("container/pocket_jukebox/slot_playing");
    private static final ResourceLocation SLOT_PAUSED_TEXTURE = MusicalFeature.id("container/pocket_jukebox/slot_paused");

    private static final Component TEXT_FULL = Component.translatable("item.muft.pocket_jukebox.full");
    private static final Component TEXT_EMPTY = Component.translatable("item.muft.pocket_jukebox.empty");
    private static final Component TEXT_EMPTY_DESCRIPTION = Component.translatable("item.muft.pocket_jukebox.empty.description");
    private static final int TEXT_COLOR = 0xFFABAAAB;

    private final PocketJukeboxComponent jukeboxContents;
    private final RenderState state = new RenderState();

    public PocketJukeboxTooltipComponent(PocketJukeboxComponent jukeboxContents) {
        this.jukeboxContents = jukeboxContents;
    }

    public @Override int getHeight() { return jukeboxContents.isEmpty() ? getHeightOfEmpty() : getHeightOfNonEmpty(); }
    public @Override int getWidth(Font textRenderer) { return ROW_WIDTH; }

    private int getHeightOfNonEmpty() { return this.getRowsHeight() + PROGRESS_BORDER_HEIGHT + 2 * Y_MARGIN; }
    private int getRowsHeight() { return this.getRows() * SLOT_DIMENSION; }
    private int getXMargin(int width) { return (width - ROW_WIDTH) / 2; }
    private int getRows() { return Mth.positiveCeilDiv(getNumVisibleSlots(), SLOTS_PER_ROW); }
    private int getNumVisibleSlots() { return Math.min(MAX_SLOT_COUNT, jukeboxContents.size()); }

    public @Override void drawItems(Font textRenderer, int x, int y, int width, int height, GuiGraphics context) {
        if (jukeboxContents.isEmpty()) { drawEmptyTooltip(textRenderer, x, y, width, context); return; }
        drawNonEmptyTooltip(textRenderer, x, y, width, context);
    }

    private void drawEmptyTooltip(Font textRenderer, int x, int y, int width, GuiGraphics context) {
        int drawX = x + getXMargin(width);
        drawEmptyDescription(drawX, y, textRenderer, context);
        drawProgressBar(drawX, y + getDescriptionHeight(textRenderer) + Y_MARGIN, textRenderer, context);
    }

    private static void drawEmptyDescription(int x, int y, Font textRenderer, GuiGraphics context) {
        for (FormattedCharSequence orderedText: textRenderer.split(TEXT_EMPTY_DESCRIPTION, ROW_WIDTH)) {
            context.drawString(textRenderer, orderedText, x, y, TEXT_COLOR, true);
            y += 9;
        }
    }

    private void drawNonEmptyTooltip(Font textRenderer, int x, int y, int width, GuiGraphics context) {
        int rowCount = getRows();

        int i = x + getXMargin(width) + ROW_WIDTH;
        int j = y + rowCount * SLOT_DIMENSION;

        for (int l = jukeboxContents.size() - 1; l >= 0; l--) {
            int rowIndex = (l / SLOTS_PER_ROW) + 1;
            int columnIndex = (l % SLOTS_PER_ROW) + 1;
            int n = i - columnIndex * SLOT_DIMENSION;
            int o = j - rowIndex * SLOT_DIMENSION;
            drawItem(l, n, o, l, textRenderer, context);
        }

        if (MuftConfigManager.config().renderSelectedDisc()) drawPlaying(context, i, j);

        drawSelectedItemTooltip(textRenderer, context, x, y, width);
        drawProgressBar(x + getXMargin(width), y + getRowsHeight() + Y_MARGIN, textRenderer, context);
    }

    private void drawPlaying(GuiGraphics context, int i, int j) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        var manager = PocketJukeboxManager.get(player);
        var discId = manager.getDiscId(jukeboxContents);
        if (discId < 0) return;
        var texture = manager.playingId() == jukeboxContents.playbackId() ? SLOT_PLAYING_TEXTURE : SLOT_PAUSED_TEXTURE;

        int rowIndex = (discId / SLOTS_PER_ROW) + 1;
        int columnIndex = (discId % SLOTS_PER_ROW) + 1;
        int n = i - columnIndex * SLOT_DIMENSION;
        int o = j - rowIndex * SLOT_DIMENSION;

        context.blitSprite(texture, n, o, SLOT_DIMENSION, SLOT_DIMENSION);
    }

    private void drawItem(int index, int x, int y, int seed, Font textRenderer, GuiGraphics context) {
        boolean isSelected = index == jukeboxContents.selectedIndex();
        var itemEntry = jukeboxContents.get(index);
        if (isSelected) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            context.blitSprite(SLOT_HIGHLIGHT_BACK_TEXTURE, x, y, SLOT_DIMENSION, SLOT_DIMENSION);
            RenderSystem.disableBlend();
        } else context.blitSprite(SLOT_BACKGROUND_TEXTURE, x, y, SLOT_DIMENSION, SLOT_DIMENSION);

        RenderHelper.drawItem(context, itemEntry, x + Y_MARGIN, y + Y_MARGIN, seed);
        RenderHelper.drawStackOverlay(context, textRenderer, itemEntry, x + Y_MARGIN, y + Y_MARGIN);

        if (!isSelected) return;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        context.blitSprite(SLOT_HIGHLIGHT_FRONT_TEXTURE, x, y, SLOT_DIMENSION, SLOT_DIMENSION);
        RenderSystem.disableBlend();
    }

    private void drawSelectedItemTooltip(Font textRenderer, GuiGraphics drawContext, int x, int y, int width) {
        if (!jukeboxContents.hasSelected()) return;
        state.update(textRenderer, jukeboxContents);

        var j = ((width) - state.width) / 2;
        drawContext.renderComponentTooltip(textRenderer, state.tooltip, x + j - 12, y + state.yOffset - 15);
    }

    private void drawProgressBar(int x, int y, Font textRenderer, GuiGraphics context) {
        context.blitSprite(getProgressFillTexture(), x + PROGRESS_BORDER_MARGIN, y + PROGRESS_BORDER_MARGIN, getProgressFill(), PROGRESS_BAR_HEIGHT);
        context.blitSprite(PROGRESS_BORDER_TEXTURE, x, y, PROGRESS_BORDER_WIDTH, PROGRESS_BORDER_HEIGHT);
        Component text = getProgressLabel();
        if (text != null) context.drawCenteredString(textRenderer, text, x + PROGRESS_BORDER_WIDTH / 2, y + 3, CommonColors.WHITE);
    }

    private static int getHeightOfEmpty() {
        return getDescriptionHeight(Minecraft.getInstance().font) + PROGRESS_BORDER_HEIGHT + 2 * Y_MARGIN;
    }

    private static int getDescriptionHeight(Font textRenderer) {
        return textRenderer.split(TEXT_EMPTY_DESCRIPTION, ROW_WIDTH).size() * 9;
    }

    private ResourceLocation getProgressFillTexture() {
        return jukeboxContents.isFull() ? PROGRESS_FULL_TEXTURE : PROGRESS_FILL_TEXTURE;
    }

    private int getProgressFill() {
        float width = PROGRESS_BAR_WIDTH * ((float) jukeboxContents.size() / MAX_SLOT_COUNT);
        return Mth.clamp((int) width, 0, PROGRESS_BAR_WIDTH);
    }

    private @Nullable Component getProgressLabel() {
        return jukeboxContents.isEmpty() ? TEXT_EMPTY : (jukeboxContents.isFull() ? TEXT_FULL : null);
    }

    private static class RenderState {
        private PocketJukeboxComponent previous;
        private final Item.TooltipContext tooltipContext = Item.TooltipContext.of(Minecraft.getInstance().level);

        private List<Component> tooltip;
        private int yOffset;
        private int width;

        public void update(Font textRenderer, PocketJukeboxComponent component) {
            if (component == previous) return;
            loadTooltip(textRenderer, component);
            previous = component;
        }

        private void loadTooltip(Font textRenderer, PocketJukeboxComponent component) {
            var itemEntry = component.getSelected();
            Component text = itemEntry.name().copy().withStyle(itemEntry.rarity().color());

            if (MuftConfigManager.config().betterJukeboxTooltip()) {
                var jukeboxSong = itemEntry.get(DataComponents.JUKEBOX_PLAYABLE);
                if (jukeboxSong == null) {
                    tooltip = List.of(text);
                    return;
                }

                tooltip = Arrays.asList(new Component[2]);
                tooltip.set(0, text);
                jukeboxSong.addToTooltip(tooltipContext, t -> tooltip.set(1, t), TooltipFlag.NORMAL);
                yOffset = -textRenderer.lineHeight - 4;

                width = Math.max(textRenderer.width(text), textRenderer.width(tooltip.get(1)));
            } else {
                tooltip = List.of(text);
                width = textRenderer.width(text);
                yOffset = 0;
            }
        }

    }
}
