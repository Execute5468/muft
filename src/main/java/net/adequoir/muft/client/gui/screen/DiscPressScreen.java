package net.adequoir.muft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.mixin.client.gui.AxDrawContext;
import net.adequoir.muft.screen.DiscPressScreenHandler;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.Function;

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
    private static final int SCROLLBAR_OFFSET_X = 126;
    private static final int SCROLLBAR_OFFSET_Y = 26;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_HEIGHT = 27;
    private static final int SCROLLBAR_AREA_HEIGHT = 131;
    private static final int MAX_TRACK_TITLE_WIDTH = WidgetButtonPage.WIDTH - (WIDGET_OFFSET_X * 2);

    private static final Identifier TEXTURE = MusicalFeature.id("textures/gui/container/disc_press.png");
    private static final Identifier DISC_SLOT_TEXTURE = MusicalFeature.id("container/disc_press/disc_empty");
    private static final Identifier DYE_SLOT_TEXTURE = MusicalFeature.id("container/disc_press/dye_empty");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = MusicalFeature.id("container/disc_press/scroller_disabled");
    private static final Identifier SCROLLER_TEXTURE = MusicalFeature.id("container/disc_press/scroller");

    private final WidgetButtonPage[] tracks = new WidgetButtonPage[TRACK_BUFFER_SIZE];
    private int selectedIndex;
    private int indexStartOffset;
    private boolean scrolling;

    private final Reference2ObjectOpenHashMap<RegistryEntry<JukeboxSong>, TextCache> descriptionCache = new Reference2ObjectOpenHashMap<>();
    private final Function<RegistryEntry<JukeboxSong>, TextCache> titleFactory = (entry) -> {
        var song = entry.value();
        var rawString = song.description().getString();
        var titlePair = stripTitle(rawString);

        var artist = Text.literal(textRenderer.trimToWidth((titlePair.getLeft()), MAX_TRACK_TITLE_WIDTH));
        var title = Text.literal(textRenderer.trimToWidth((titlePair.getRight()), MAX_TRACK_TITLE_WIDTH));
        var duration = Text.literal(StringHelper.formatTicks(song.getLengthInTicks(), 20.0F)).withColor(Colors.LIGHT_GRAY);

        return new TextCache(title, artist, List.of(song.description(), duration));
    };

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
            tracks[l] = addDrawableChild(new WidgetButtonPage(i + WIDGET_OFFSET_X, k, l, pressAction));
            k += WidgetButtonPage.HEIGHT;
        }
    }

    public @Override void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderInternal(context, mouseX, mouseY);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void renderInternal(DrawContext context, int mouseX, int mouseY) {
        var tracks = handler.tracks();
        var trackSize = tracks.size();
        if (trackSize <= 0) return;

        int i = (this.width - this.backgroundWidth) >> 1;
        int j = (this.height - this.backgroundHeight) >> 1;
        renderScrollbar(context, i, j, tracks);

        for (var widget: this.tracks) {
            widget.visible = widget.index < trackSize;
            widget.renderTooltip(context, mouseX, mouseY);
        }
        RenderSystem.enableDepthTest();
    }

    private void renderScrollbar(DrawContext context, int x, int y, RegistryEntryList<JukeboxSong> tracks) {
        int i = tracks.size() + 1 - TRACK_BUFFER_SIZE;

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
        int i = handler.tracks().size();
        if (canScroll(i)) {
            int j = i - TRACK_BUFFER_SIZE;
            indexStartOffset = MathHelper.clamp((int) (indexStartOffset - verticalAmount), 0, j);
        }
        return true;
    }

    public @Override boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!scrolling) return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        int i = handler.tracks().size();

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

        boolean bl = canScroll(handler.tracks().size())
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

    private record TextCache(Text title, Text artist, List<Text> tooltip) {}

    private static boolean canScroll(int listSize) { return listSize > TRACK_BUFFER_SIZE; }

    private class WidgetButtonPage extends ButtonWidget {
        private static final int WIDTH = 120;
        private static final int HEIGHT = 22;

        static final Identifier TEXTURE = MusicalFeature.id("container/disc_press/button");
        static final Identifier HIGHLIGHTED_TEXTURE = MusicalFeature.id("container/disc_press/button_highlighted");
        static final Identifier SELECTED_TEXTURE = MusicalFeature.id("container/disc_press/button_selected");

        static final int ARTIST_TEXT_COLOR = 0xFF685E4A;
        static final int ARTIST_SELECTED_TEXT_COLOR = 0xFFA09172;
        static final int HIGHLIGHT_TITLE_TEXT_COLOR = 0xFFFFFF80;

        final int index;

        WidgetButtonPage(int x, int y, int index, ButtonWidget.PressAction onPress) {
            super(x, y, WIDTH, HEIGHT, ScreenTexts.EMPTY, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.index = index;
            this.visible = false;
        }

        protected @Override void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            var tracks = handler.tracks();
            var m = indexStartOffset + index;
            if (m > tracks.size()) return;

            context.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();

            context.drawGuiTexture(getTexture(), getX(), getY(), getWidth(), getHeight());
            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            renderText(context, m);
        }

        private void renderText(DrawContext context, int m) {
            var tracks = handler.tracks();
            var trackEntry = tracks.get(m);

            var text = descriptionCache.computeIfAbsent(trackEntry, titleFactory);

            var isSelected = selectedIndex - indexStartOffset == index;

            var titleColor = isHovered() || isSelected ? HIGHLIGHT_TITLE_TEXT_COLOR : Colors.WHITE;
            var artistColor = isSelected ? ARTIST_SELECTED_TEXT_COLOR : ARTIST_TEXT_COLOR;

            context.drawText(textRenderer, text.title, getX() + WIDGET_OFFSET_X, getY() + WIDGET_OFFSET_Y, titleColor, true);
            context.drawText(textRenderer, text.artist, getX() + WIDGET_OFFSET_X, getY() + WIDGET_OFFSET_Y + textRenderer.fontHeight, artistColor, false);
        }

        private void renderTooltip(DrawContext context, int x, int y) {
            var tracks = handler.tracks();
            var m = selectedIndex + indexStartOffset;
            if (!hovered || m > tracks.size()) return;
            var trackEntry = tracks.get(m);
            var textCache = descriptionCache.get(trackEntry);
            if (textCache == null) return;
            context.drawTooltip(
                textRenderer,
                textCache.tooltip,
                x, y
            );
        }

        private Identifier getTexture() {
            if (!active) return SELECTED_TEXTURE;
            if (selectedIndex - indexStartOffset == index) return SELECTED_TEXTURE;
            return isHovered() ? HIGHLIGHTED_TEXTURE: TEXTURE;
        }
    }
}
