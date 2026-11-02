package net.adequoir.muft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.screen.DiscPressScreenHandler;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DiscPressScreen extends HandledScreen<DiscPressScreenHandler> {
    private static final int BACKGROUND_WIDTH = 308;
    private static final int PLAYER_INV_TITLE_X = 139;
    private static final int CATALOGUE_OFFSET_X = 65;
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int SLOT_SIZE = 16;

    private static final int TRACK_BUFFER_SIZE = 6;

    private static final int WIDGET_OFFSET_X = 5;
    private static final int WIDGET_OFFSET_Y = 3;
    private static final int WIDGET_DURATION_OFFSET_X = -31;
    private static final int SCROLLBAR_OFFSET_X = 126;
    private static final int SCROLLBAR_OFFSET_Y = 26;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_HEIGHT = 27;
    private static final int SCROLLBAR_AREA_HEIGHT = 131;

    private static final Identifier TEXTURE = MusicalFeature.id("textures/gui/container/disc_press.png");
    private static final Identifier DISC_SLOT_TEXTURE = MusicalFeature.id("container/disc_press/disc_empty");
    private static final Identifier DYE_SLOT_TEXTURE = MusicalFeature.id("container/disc_press/dye_empty");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = MusicalFeature.id("container/disc_press/scroller_disabled");
    private static final Identifier SCROLLER_TEXTURE = MusicalFeature.id("container/disc_press/scroller");

    private final WidgetButtonPage[] trackWidgets = new WidgetButtonPage[TRACK_BUFFER_SIZE];
    private final Cache cache = new Cache(handler.tracks());

    private int selectedIndex;
    private int indexStartOffset;
    private boolean scrolling;

    public DiscPressScreen(DiscPressScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = BACKGROUND_WIDTH;
        this.playerInventoryTitleX = PLAYER_INV_TITLE_X;
    }

    protected @Override void init() {
        super.init();
        this.titleX = (BACKGROUND_WIDTH / 2) + CATALOGUE_OFFSET_X - (textRenderer.getWidth(title) >> 1);

        int i = (this.width - this.backgroundWidth) >> 1;
        int j = (this.height - this.backgroundHeight) >> 1;
        int k = j + SCROLLBAR_OFFSET_Y;

        ButtonWidget.PressAction pressAction = button -> {
            if (!(button instanceof WidgetButtonPage widgetButtonPage)) return;
            selectedIndex = widgetButtonPage.index + indexStartOffset;
        };

        for (int l = 0; l < TRACK_BUFFER_SIZE; l++) {
            trackWidgets[l] = addDrawableChild(new WidgetButtonPage(i + WIDGET_OFFSET_X, k, l, pressAction));
            k += WidgetButtonPage.HEIGHT;
        }
    }

    public @Override void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderInternal(context, mouseX, mouseY);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void renderInternal(DrawContext context, int mouseX, int mouseY) {
        var trackSize = cache.size();
        if (trackSize == 0) return;

        int i = (this.width - this.backgroundWidth) >> 1;
        int j = (this.height - this.backgroundHeight) >> 1;
        renderScrollbar(context, i, j);

        for (var widget: this.trackWidgets) {
            widget.visible = widget.index < trackSize;
            widget.renderTooltip(context, mouseX, mouseY);
        }
        RenderSystem.enableDepthTest();
    }

    private void renderScrollbar(DrawContext context, int x, int y) {
        int i = cache.size() + 1 - TRACK_BUFFER_SIZE;

        int drawX = x + SCROLLBAR_OFFSET_X;
        int drawY = y + SCROLLBAR_OFFSET_Y;

        if (i <= 1) {
            context.drawGuiTexture(SCROLLER_DISABLED_TEXTURE, drawX, drawY, 0, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
            return;
        }

        int j = SCROLLBAR_AREA_HEIGHT - (SCROLLBAR_HEIGHT + (i - 1) * SCROLLBAR_AREA_HEIGHT / i);
        int k = 1 + j / i + SCROLLBAR_AREA_HEIGHT / i;
        int l = 105;
        int m = Math.min(l, indexStartOffset * k);
        if (indexStartOffset == i - 1) m = l;

        context.drawGuiTexture(SCROLLER_TEXTURE, drawX, drawY + m, 0, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
    }

    protected @Override void drawBackground(DrawContext ctx, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) >> 1;
        int j = (this.height - this.backgroundHeight) >> 1;
        ctx.drawTexture(TEXTURE, i, j, 0, 0.0F, 0.0F, backgroundWidth, backgroundHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        drawSlot(ctx, handler.dyeSlot(), DYE_SLOT_TEXTURE, i, j);
        drawSlot(ctx, handler.discSlot(), DISC_SLOT_TEXTURE, i, j);
    }

    private static void drawSlot(DrawContext context, Slot slot, Identifier texture, int x, int y) {
        if (slot.hasStack()) return;
        context.drawGuiTexture(texture, x + slot.x, y + slot.y, SLOT_SIZE, SLOT_SIZE);
    }

    public @Override boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int i = cache.size();
        if (canScroll(i)) {
            int j = i - TRACK_BUFFER_SIZE;
            indexStartOffset = MathHelper.clamp((int) (indexStartOffset - verticalAmount), 0, j);
        }
        return true;
    }

    public @Override boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!scrolling) return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        int i = cache.size();

        int j = y + SCROLLBAR_OFFSET_Y;
        int k = j + SCROLLBAR_AREA_HEIGHT;
        int l = i - TRACK_BUFFER_SIZE;
        float f = ((float) mouseY - j - 13.5F) / (k - j - 27.0F);
        f = f * l + 0.5F;
        indexStartOffset = MathHelper.clamp((int) f, 0, l);
        return true;
    }

    public @Override boolean mouseClicked(double mouseX, double mouseY, int button) {
        scrolling = false;
        int i = (this.width - this.backgroundWidth) >> 1;
        int j = (this.height - this.backgroundHeight) >> 1;

        boolean bl = canScroll(cache.size())
                && mouseX > i + SCROLLBAR_OFFSET_X
                && mouseX < i + SCROLLBAR_OFFSET_X + SCROLLBAR_WIDTH
                && mouseY > j + SCROLLBAR_OFFSET_Y
                && mouseY <= j + SCROLLBAR_OFFSET_Y + SCROLLBAR_AREA_HEIGHT + 1;
        if (bl) scrolling = true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static Pair<String, String> stripTitle(String description) {
        int delimIndex = description.indexOf('-');
        if (delimIndex < 0) return new Pair<>("", description);

        var i = delimIndex + 1;
        while (i < description.length() && Character.isWhitespace(description.charAt(i))) i++;

        var j = delimIndex - 1;
        while (j > 0 && Character.isWhitespace(description.charAt(j))) j--;

        return i >= description.length()
                ? new Pair<>("", description)
                : new Pair<>(description.substring(0, j + 1), description.substring(i));
    }

    private static boolean canScroll(int listSize) { return listSize > TRACK_BUFFER_SIZE; }

    private class WidgetButtonPage extends ButtonWidget {
        private static final int WIDTH = 120;
        private static final int HEIGHT = 22;

        static final Identifier TEXTURE = MusicalFeature.id("container/disc_press/button");
        static final Identifier HIGHLIGHTED_TEXTURE = MusicalFeature.id("container/disc_press/button_highlighted");
        static final Identifier SELECTED_TEXTURE = MusicalFeature.id("container/disc_press/button_selected");

        static final int METADATA_COLOR = 0xFF685E4A;
        static final int METADATA_HOVERED_COLOR = 0xFF51493A;
        static final int METADATA_SELECTED_COLOR = 0xFFA09172;
        static final int HIGHLIGHT_TITLE_TEXT_COLOR = 0xFFFFFF80;

        final int index;

        WidgetButtonPage(int x, int y, int index, ButtonWidget.PressAction onPress) {
            super(x, y, WIDTH, HEIGHT, ScreenTexts.EMPTY, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.index = index;
            this.visible = false;
        }

        protected @Override void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            var m = indexStartOffset + index;
            if (m > cache.size()) return;

            context.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();

            context.drawGuiTexture(getTexture(), getX(), getY(), getWidth(), getHeight());
            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            renderText(context, m);
        }

        private void renderText(DrawContext context, int m) {
            var isSelected = selectedIndex - indexStartOffset == index;

            var titleColor = isHovered() || isSelected ? HIGHLIGHT_TITLE_TEXT_COLOR : Colors.WHITE;
            var metadataColor = isSelected
                    ? METADATA_SELECTED_COLOR
                    : (isHovered() ? METADATA_HOVERED_COLOR : METADATA_COLOR);

            var title = cache.getTitle(m);
            var artist = cache.getArtist(m);
            var duration = cache.getDuration(m);

            context.drawText(textRenderer, title, getX() + WIDGET_OFFSET_X, getY() + WIDGET_OFFSET_Y, titleColor, true);
            context.drawText(textRenderer, artist, getX() + WIDGET_OFFSET_X, getY() + WIDGET_OFFSET_Y + textRenderer.fontHeight, metadataColor, false);
            context.drawText(textRenderer, duration, getX() + WIDTH + WIDGET_DURATION_OFFSET_X, getY() + WIDGET_OFFSET_Y + (textRenderer.fontHeight >> 1), metadataColor, false);
        }

        private void renderTooltip(DrawContext context, int x, int y) {
            var m = index + indexStartOffset;
            if (!hovered || m > cache.size()) return;
            context.drawTooltip(
                textRenderer,
                cache.getTooltip(m),
                x, y
            );
        }

        private Identifier getTexture() {
            if (!active) return SELECTED_TEXTURE;
            if (selectedIndex - indexStartOffset == index) return SELECTED_TEXTURE;
            return isHovered() ? HIGHLIGHTED_TEXTURE: TEXTURE;
        }
    }

    private static class Cache {
        private static final int MAX_TEXT_WIDTH = 80;
        private static final int MAX_TRUNCATED_WIDTH = MAX_TEXT_WIDTH - MinecraftClient.getInstance().textRenderer.getWidth("...");
        private static final Text UNKNOWN_ARTIST = Text.literal("Unknown");

        private final @Nullable Text[] titles;
        private final @Nullable Text[] artists;
        private final @Nullable Text[] durations;
        private final @Nullable List<Text>[] tooltips;
        private final List<RegistryEntry<JukeboxSong>> entries;

        Cache(List<RegistryEntry<JukeboxSong>> songEntries) {
            int size = songEntries.size();
            //noinspection unchecked
            tooltips = (List<Text>[]) new List<?>[size];
            titles = new Text[size];
            artists = new Text[size];
            durations = new Text[size];
            entries = songEntries;
        }

        int size() { return entries.size(); }

        Text getTitle(int index) {
            var title = titles[index];
            return title != null ? title : loadTexts(index).getTitle(index);
        }

        Text getArtist(int index) {
            var artist = artists[index];
            return artist != null ? artist : loadTexts(index).getArtist(index);
        }

        Text getDuration(int index) {
            var duration = durations[index];
            return duration != null ? duration : loadTexts(index).getDuration(index);
        }

        List<Text> getTooltip(int index) {
            var tooltip = tooltips[index];
            return tooltip != null ? tooltip : loadTexts(index).getTooltip(index);
        }

        private Cache loadTexts(int index) {
            var song = entries.get(index).value();

            var duration = Text.literal(StringHelper.formatTicks(song.getLengthInTicks(), 20.0F));
            var description = song.description();
            var tooltip = List.of(description, duration.copy().withColor(Colors.LIGHT_GRAY));

            var fullTitle = description.getString();
            int delimIndex = fullTitle.indexOf('-');
            if (delimIndex < 0) {
                setTexts(index, entryTextOf(fullTitle), UNKNOWN_ARTIST, duration, tooltip);
                return this;
            }

            var i = delimIndex + 1;
            while (i < fullTitle.length() && Character.isWhitespace(fullTitle.charAt(i))) i++;

            var j = delimIndex - 1;
            while (j > 0 && Character.isWhitespace(fullTitle.charAt(j))) j--;

            setTexts(
                index,
                entryTextOf(fullTitle.substring(i)),
                entryTextOf(fullTitle.substring(0, j + 1)),
                duration,
                tooltip
            );
            return this;
        }

        private static Text entryTextOf(String string) {
            var textRenderer = MinecraftClient.getInstance().textRenderer;

            if (textRenderer.getWidth(string) < MAX_TEXT_WIDTH) return Text.literal(string);
            var truncatedString = textRenderer.trimToWidth(string, MAX_TRUNCATED_WIDTH) + "...";
            return Text.literal(truncatedString);
        }

        private void setTexts(int index, Text title, Text artist, Text duration, List<Text> tooltip) {
            titles[index] = title;
            artists[index] = artist;
            durations[index] = duration;
            tooltips[index] = tooltip;
        }
    }
}
