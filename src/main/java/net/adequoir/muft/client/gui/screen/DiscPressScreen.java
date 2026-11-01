package net.adequoir.muft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.screen.DiscPressScreenHandler;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.function.Function;

public class DiscPressScreen extends HandledScreen<DiscPressScreenHandler> {
    private static final int BACKGROUND_WIDTH = 276;
    private static final int PLAYER_INV_TITLE_X = 107;
    private static final int CATALOGUE_OFFSET_X = 49;
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int SLOT_SIZE = 16;

    private static final int TRACK_BUFFER_SIZE = 5;
    private static final int TRACK_TITLE_OFFSET_X = 5;

    private static final int WIDGET_OFFSET_X = 5;
    private static final int SCROLLBAR_OFFSET_X = 94;
    private static final int SCROLLBAR_OFFSET_Y = 18;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_HEIGHT = 27;
    private static final int SCROLLBAR_AREA_HEIGHT = 139;
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

    private final Reference2ObjectOpenHashMap<RegistryEntry<JukeboxSong>, Text> descriptionCache = new Reference2ObjectOpenHashMap<>();
    private final Function<RegistryEntry<JukeboxSong>, Text> titleFactory = (entry) -> {
        var rawString = entry.value().description().getString();
        var title = stripTitle(rawString);
        return Text.literal(textRenderer.trimToWidth((title), MAX_TRACK_TITLE_WIDTH));
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
        renderInternal(context);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void renderInternal(DrawContext context) {
        var tracks = handler.tracks();
        var trackSize = tracks.size();
        if (trackSize <= 0) return;

        int i = (this.width - this.backgroundWidth) >> 1;
        int j = (this.height - this.backgroundHeight) >> 1;
        int k = j + SLOT_SIZE + 1;
        int l = i + WIDGET_OFFSET_X + TRACK_TITLE_OFFSET_X;
        renderScrollbar(context, i, j, tracks);

        for (int m = 0; m < tracks.size(); m++) {
            if (canScroll(trackSize) && (m < indexStartOffset || m >= TRACK_BUFFER_SIZE + indexStartOffset)) continue;
            var trackEntry = tracks.get(m);

            int n = k + 7;

            var description = descriptionCache.computeIfAbsent(trackEntry, titleFactory);

            context.drawText(textRenderer, description, l, n, Colors.WHITE, true);
            k += WidgetButtonPage.HEIGHT;
        }

        for (var widget: this.tracks) widget.visible = widget.index < trackSize;
        RenderSystem.enableDepthTest();
    }

    private static String stripTitle(String description) {
        int delimIndex = description.indexOf('-');
        if (delimIndex < 0) return description;

        var i = delimIndex + 1;
        while (i < description.length() && Character.isWhitespace(description.charAt(i))) i++;
        return i >= description.length() ? description : description.substring(i);
    }

    private void renderScrollbar(DrawContext context, int x, int y, RegistryEntryList<JukeboxSong   > tracks) {
        int i = tracks.size() + 1 - TRACK_BUFFER_SIZE;

        int drawX = x + SCROLLBAR_OFFSET_X;
        int drawY = y + SCROLLBAR_OFFSET_Y;

        if (i <= 1) {
            context.drawGuiTexture(SCROLLER_DISABLED_TEXTURE, drawX, drawY, 0, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
            return;
        }

        int j = SCROLLBAR_AREA_HEIGHT - (SCROLLBAR_HEIGHT + (i - 1) * SCROLLBAR_AREA_HEIGHT / i);
        int k = 1 + j / i + SCROLLBAR_AREA_HEIGHT / i;
        int l = 113;
        int m = Math.min(l, indexStartOffset * k);
        if (indexStartOffset == i - 1) m = l;

        context.drawGuiTexture(SCROLLER_TEXTURE, drawX, drawY + m, 0, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
    }

    protected @Override void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
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
        int i = (this.width - this.backgroundHeight) >> 1;
        int j = (this.height - this.backgroundHeight) >> 1;

        boolean bl = canScroll(handler.tracks().size())
                && mouseX > i + SCROLLBAR_OFFSET_X
                && mouseX < i + SCROLLBAR_OFFSET_X + SCROLLBAR_WIDTH
                && mouseY > j + SCROLLBAR_OFFSET_Y
                && mouseY <= j + SCROLLBAR_OFFSET_Y + SCROLLBAR_AREA_HEIGHT + 1;
        if (bl) scrolling = true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static boolean canScroll(int listSize) { return listSize > TRACK_BUFFER_SIZE; }

    private static class WidgetButtonPage extends ButtonWidget {
        private static final int WIDTH = 88;
        private static final int HEIGHT = 28;

        final int index;

        WidgetButtonPage(int x, int y, int index, ButtonWidget.PressAction onPress) {
            super(x, y, WIDTH, HEIGHT, ScreenTexts.EMPTY, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.index = index;
            this.visible = false;
        }
    }
}
