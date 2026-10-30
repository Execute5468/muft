package net.adequoir.muft.client.gui.tooltip;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.client.util.RenderHelper;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.mixin.client.gui.AxDrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

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

    private static final Identifier PROGRESS_BORDER_TEXTURE = MusicalFeature.id("container/pocket_jukebox/pocket_jukebox_progressbar_border");
    private static final Identifier PROGRESS_FILL_TEXTURE = MusicalFeature.id("container/pocket_jukebox/pocket_jukebox_progressbar_fill");
    private static final Identifier PROGRESS_FULL_TEXTURE = MusicalFeature.id("container/pocket_jukebox/pocket_jukebox_progressbar_full");
    private static final Identifier SLOT_HIGHLIGHT_BACK_TEXTURE = MusicalFeature.id("container/pocket_jukebox/slot_highlight_back");
    private static final Identifier SLOT_HIGHLIGHT_FRONT_TEXTURE = MusicalFeature.id("container/pocket_jukebox/slot_highlight_front");
    private static final Identifier SLOT_BACKGROUND_TEXTURE = MusicalFeature.id("container/pocket_jukebox/slot_background");

    private static final Text TEXT_FULL = Text.translatable("item.muft.pocket_jukebox.full");
    private static final Text TEXT_EMPTY = Text.translatable("item.muft.pocket_jukebox.empty");
    private static final Text TEXT_EMPTY_DESCRIPTION = Text.translatable("item.muft.pocket_jukebox.empty.description");
    private static final int TEXT_COLOR = 0xFFABAAAB;

    private final PocketJukeboxComponent jukeboxContents;

    public PocketJukeboxTooltipComponent(PocketJukeboxComponent jukeboxContents) { this.jukeboxContents = jukeboxContents; }

    public @Override int getHeight() { return jukeboxContents.isEmpty() ? getHeightOfEmpty() : getHeightOfNonEmpty(); }
    public @Override int getWidth(TextRenderer textRenderer) { return ROW_WIDTH; }

    private int getHeightOfNonEmpty() { return this.getRowsHeight() + PROGRESS_BORDER_HEIGHT + 2 * Y_MARGIN; }
    private int getRowsHeight() { return this.getRows() * SLOT_DIMENSION; }
    private int getXMargin(int width) { return (width - ROW_WIDTH) / 2; }
    private int getRows() { return MathHelper.ceilDiv(getNumVisibleSlots(), SLOTS_PER_ROW); }
    private int getNumVisibleSlots() { return Math.min(MAX_SLOT_COUNT, jukeboxContents.size()); }

    public @Override void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        if (jukeboxContents.isEmpty()) { drawEmptyTooltip(textRenderer, x, y, width, context); return; }
        drawNonEmptyTooltip(textRenderer, x, y, width, context);
    }

    private void drawEmptyTooltip(TextRenderer textRenderer, int x, int y, int width, DrawContext context) {
        int drawX = x + getXMargin(width);
        drawEmptyDescription(drawX, y, textRenderer, context);
        drawProgressBar(drawX, y + getDescriptionHeight(textRenderer) + Y_MARGIN, textRenderer, context);
    }

    private static void drawEmptyDescription(int x, int y, TextRenderer textRenderer, DrawContext context) {
        for (OrderedText orderedText: textRenderer.wrapLines(TEXT_EMPTY_DESCRIPTION, ROW_WIDTH)) {
            context.drawText(textRenderer, orderedText, x, y, TEXT_COLOR, true);
            y += 9;
        }
    }

    private void drawNonEmptyTooltip(TextRenderer textRenderer, int x, int y, int width, DrawContext context) {
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

        drawSelectedItemTooltip(textRenderer, context, x, y, width);
        drawProgressBar(x + getXMargin(width), y + getRowsHeight() + Y_MARGIN, textRenderer, context);
    }

    private void drawItem(int index, int x, int y, int seed, TextRenderer textRenderer, DrawContext context) {
        boolean isSelected = index == jukeboxContents.selectedIndex();
        var texture = isSelected ? SLOT_HIGHLIGHT_BACK_TEXTURE : SLOT_BACKGROUND_TEXTURE;
        var itemEntry = jukeboxContents.get(index);

        context.drawGuiTexture(texture, x, y, SLOT_DIMENSION, SLOT_DIMENSION);
        RenderHelper.drawItem(context, itemEntry, x + Y_MARGIN, y + Y_MARGIN, seed);
        RenderHelper.drawStackOverlay(context, textRenderer, itemEntry, x + Y_MARGIN, y + Y_MARGIN);
        if (!isSelected) return;
        context.drawGuiTexture(SLOT_HIGHLIGHT_FRONT_TEXTURE, x, y, SLOT_DIMENSION, SLOT_DIMENSION);
    }

    private void drawSelectedItemTooltip(TextRenderer textRenderer, DrawContext drawContext, int x, int y, int width) {
        if (!jukeboxContents.hasSelected()) return;
        var itemEntry = jukeboxContents.getSelected();
        Text text = itemEntry.name();
        OrderedText orderedText = text.asOrderedText();
        int i = textRenderer.getWidth(orderedText);
        int j = x + width / 2 - 12;
        TooltipComponent tooltipComponent = TooltipComponent.of(orderedText);
        ((AxDrawContext) drawContext).axDrawTooltip(
            textRenderer,
            List.of(tooltipComponent),
            j - i / 2, y - 15,
            HoveredTooltipPositioner.INSTANCE
        );
    }

    private void drawProgressBar(int x, int y, TextRenderer textRenderer, DrawContext context) {
        context.drawGuiTexture(getProgressFillTexture(), x + PROGRESS_BORDER_MARGIN, y + PROGRESS_BORDER_MARGIN, getProgressFill(), PROGRESS_BAR_HEIGHT);
        context.drawGuiTexture(PROGRESS_BORDER_TEXTURE, x, y, PROGRESS_BORDER_WIDTH, PROGRESS_BORDER_HEIGHT);
        Text text = getProgressLabel();
        if (text != null) context.drawCenteredTextWithShadow(textRenderer, text, x + PROGRESS_BORDER_WIDTH / 2, y + 3, Colors.WHITE);
    }

    private static int getHeightOfEmpty() {
        return getDescriptionHeight(MinecraftClient.getInstance().textRenderer) + PROGRESS_BORDER_HEIGHT + 2 * Y_MARGIN;
    }

    private static int getDescriptionHeight(TextRenderer textRenderer) {
        return textRenderer.wrapLines(TEXT_EMPTY_DESCRIPTION, ROW_WIDTH).size() * 9;
    }

    private Identifier getProgressFillTexture() {
        return jukeboxContents.isFull() ? PROGRESS_FULL_TEXTURE : PROGRESS_FILL_TEXTURE;
    }

    private int getProgressFill() {
        float width = PROGRESS_BAR_WIDTH * ((float) jukeboxContents.size() / MAX_SLOT_COUNT);
        return MathHelper.clamp((int) width, 0, PROGRESS_BAR_WIDTH);
    }

    private @Nullable Text getProgressLabel() {
        return jukeboxContents.isEmpty() ? TEXT_EMPTY : (jukeboxContents.isFull() ? TEXT_FULL : null);
    }
}
