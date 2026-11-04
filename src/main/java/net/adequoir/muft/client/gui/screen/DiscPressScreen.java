package net.adequoir.muft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.api.client.network.ClientNetworkHelper;
import net.adequoir.muft.network.DiscPressSelectPayload;
import net.adequoir.muft.screen.DiscPressScreenHandler;
import net.adequoir.muft.util.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.JukeboxSong;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class DiscPressScreen extends AbstractContainerScreen<DiscPressScreenHandler> {
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

    private static final int NAME_FIELD_OFFSET_X = 7;
    private static final int NAME_FIELD_OFFSET_Y = 10;
    private static final int NAME_FIELD_WIDTH = 103;
    private static final int NAME_FIELD_HEIGHT = 12;

    private static final ResourceLocation TEXTURE = MusicalFeature.id("textures/gui/container/disc_press.png");
    private static final ResourceLocation DISC_SLOT_TEXTURE = MusicalFeature.id("container/disc_press/disc_empty");
    private static final ResourceLocation DYE_SLOT_TEXTURE = MusicalFeature.id("container/disc_press/dye_empty");
    private static final ResourceLocation SCROLLER_DISABLED_TEXTURE = MusicalFeature.id("container/disc_press/scroller_disabled");
    private static final ResourceLocation SCROLLER_TEXTURE = MusicalFeature.id("container/disc_press/scroller");

    private final WidgetButtonPage[] trackWidgets = new WidgetButtonPage[TRACK_BUFFER_SIZE];
    private EditBox nameField;
    private final Cache cache;

    private int selectedId;
    private int indexStartOffset;
    private boolean scrolling;

    public DiscPressScreen(DiscPressScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageWidth = BACKGROUND_WIDTH;
        this.inventoryLabelX = PLAYER_INV_TITLE_X;
        this.cache = new Cache(handler.tracks());
        if (!cache.isEmpty()) selectId(cache.idOf(0));
    }

    protected @Override void init() {
        super.init();
        this.titleLabelX = (BACKGROUND_WIDTH / 2) + CATALOGUE_OFFSET_X - (font.width(title) >> 1);

        int i = (this.width - this.imageWidth) >> 1;
        int j = (this.height - this.imageHeight) >> 1;
        int k = j + SCROLLBAR_OFFSET_Y;


        var sortButton = new SortButton(i, j, new SortButtonState());
        addRenderableWidget(sortButton);

        Button.OnPress pressAction = button -> {
            if (!(button instanceof WidgetButtonPage widgetButtonPage)) return;
            selectId(cache.idOf(widgetButtonPage.index + indexStartOffset));
        };

        for (int l = 0; l < TRACK_BUFFER_SIZE; l++) {
            trackWidgets[l] = addRenderableWidget(new WidgetButtonPage(i + WIDGET_OFFSET_X, k, l, pressAction));
            k += WidgetButtonPage.HEIGHT;
        }
        initNameField(i, j);
    }

    private void initNameField(int x, int y) {
        nameField = new EditBox(font, x + NAME_FIELD_OFFSET_X, y + NAME_FIELD_OFFSET_Y, NAME_FIELD_WIDTH, NAME_FIELD_HEIGHT, Component.empty());
        nameField.setCanLoseFocus(false);
        nameField.setTextColor(-1);
        nameField.setTextColorUneditable(-1);
        nameField.setBordered(false);
        nameField.setMaxLength(50);
        nameField.setValue("");
        nameField.setEditable(true);
        nameField.setResponder(this::onRenamed);
        addWidget(nameField);
    }

    private void selectId(int id) {
        selectedId = id;
        menu.setSelectedIndex(id);
        ClientNetworkHelper.send(new DiscPressSelectPayload(id));
    }

    private void onRenamed(String name) {
        indexStartOffset = 0;
        cache.updateFilter(name);
    }

    public @Override void resize(Minecraft client, int width, int height) {
        var string = nameField.getValue();
        super.resize(client, width, height);
        nameField.setValue(string);
    }

    public @Override boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && minecraft != null && minecraft.player != null) minecraft.player.closeContainer();

        var bl = nameField.keyPressed(keyCode, scanCode, modifiers) || nameField.canConsumeInput();
        return bl || super.keyPressed(keyCode, scanCode, modifiers);
    }

    public @Override void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderInternal(context, mouseX, mouseY);
        nameField.render(context, mouseX, mouseY, delta);
        renderTooltip(context, mouseX, mouseY);
    }

    private void renderInternal(GuiGraphics context, int mouseX, int mouseY) {
        var trackSize = cache.size();
        if (trackSize == 0) return;

        int i = (this.width - this.imageWidth) >> 1;
        int j = (this.height - this.imageHeight) >> 1;
        renderScrollbar(context, i, j);

        for (var widget: this.trackWidgets) {
            widget.visible = widget.index < trackSize;
            widget.renderTooltip(context, mouseX, mouseY);
        }
        RenderSystem.enableDepthTest();
    }

    private void renderScrollbar(GuiGraphics context, int x, int y) {
        int i = cache.size() + 1 - TRACK_BUFFER_SIZE;

        int drawX = x + SCROLLBAR_OFFSET_X;
        int drawY = y + SCROLLBAR_OFFSET_Y;

        if (i <= 1) {
            context.blitSprite(SCROLLER_DISABLED_TEXTURE, drawX, drawY, 0, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
            return;
        }

        int j = SCROLLBAR_AREA_HEIGHT - (SCROLLBAR_HEIGHT + (i - 1) * SCROLLBAR_AREA_HEIGHT / i);
        int k = 1 + j / i + SCROLLBAR_AREA_HEIGHT / i;
        int l = 105;
        int m = Math.min(l, indexStartOffset * k);
        if (indexStartOffset == i - 1) m = l;

        context.blitSprite(SCROLLER_TEXTURE, drawX, drawY + m, 0, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
    }

    protected @Override void renderBg(GuiGraphics ctx, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) >> 1;
        int j = (this.height - this.imageHeight) >> 1;
        ctx.blit(TEXTURE, i, j, 0, 0.0F, 0.0F, imageWidth, imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        drawSlot(ctx, menu.dyeSlot(), DYE_SLOT_TEXTURE, i, j);
        drawSlot(ctx, menu.discSlot(), DISC_SLOT_TEXTURE, i, j);
    }

    private static void drawSlot(GuiGraphics context, Slot slot, ResourceLocation texture, int x, int y) {
        if (slot.hasItem()) return;
        context.blitSprite(texture, x + slot.x, y + slot.y, SLOT_SIZE, SLOT_SIZE);
    }

    public @Override boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int i = cache.size();
        if (canScroll(i)) {
            int j = i - TRACK_BUFFER_SIZE;
            indexStartOffset = Mth.clamp((int) (indexStartOffset - verticalAmount), 0, j);
        }
        return true;
    }

    public @Override boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!scrolling) return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        int i = cache.size();

        int j = topPos + SCROLLBAR_OFFSET_Y;
        int k = j + SCROLLBAR_AREA_HEIGHT;
        int l = i - TRACK_BUFFER_SIZE;
        float f = ((float) mouseY - j - 13.5F) / (k - j - 27.0F);
        f = f * l + 0.5F;
        indexStartOffset = Mth.clamp((int) f, 0, l);
        return true;
    }

    public @Override boolean mouseClicked(double mouseX, double mouseY, int button) {
        scrolling = false;
        int i = (this.width - this.imageWidth) >> 1;
        int j = (this.height - this.imageHeight) >> 1;

        boolean bl = canScroll(cache.size())
                && mouseX > i + SCROLLBAR_OFFSET_X
                && mouseX < i + SCROLLBAR_OFFSET_X + SCROLLBAR_WIDTH
                && mouseY > j + SCROLLBAR_OFFSET_Y
                && mouseY <= j + SCROLLBAR_OFFSET_Y + SCROLLBAR_AREA_HEIGHT + 1;
        if (bl) scrolling = true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static Tuple<String, String> stripTitle(String description) {
        int delimIndex = description.indexOf('-');
        if (delimIndex < 0) return new Tuple<>("", description);

        var i = delimIndex + 1;
        while (i < description.length() && Character.isWhitespace(description.charAt(i))) i++;

        var j = delimIndex - 1;
        while (j > 0 && Character.isWhitespace(description.charAt(j))) j--;

        return i >= description.length()
                ? new Tuple<>("", description)
                : new Tuple<>(description.substring(0, j + 1), description.substring(i));
    }

    private static boolean canScroll(int listSize) { return listSize > TRACK_BUFFER_SIZE; }

    private class SortButtonState implements Button.OnPress {
        private SortType sortType = SortType.TITLE_ASCENDING;

        public @Override void onPress(Button button) {
            sortType = sortType.next();
            sortType.sort(cache, nameField.getValue());
        }

        private void renderTooltip(GuiGraphics context, int x, int y) {
            context.renderComponentTooltip(
                font,
                sortType.tooltip,
                x, y
            );
        }
    }

    private static class SortButton extends Button {
        private static final int SIZE = 14;
        private static final int OFFSET_X = 118;
        private static final int OFFSET_Y = 7;
        private static final ResourceLocation TEXTURE = MusicalFeature.id("container/disc_press/sort_button");
        private static final ResourceLocation HIGHLIGHTED_TEXTURE = MusicalFeature.id("container/disc_press/sort_button_highlighted");

        protected SortButton(int x, int y, SortButtonState state) {
            super(x + OFFSET_X, y + OFFSET_Y, SIZE, SIZE, Component.empty(), state, DEFAULT_NARRATION);
        }

        protected @Override void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
            if (isHovered()) {
                context.blitSprite(HIGHLIGHTED_TEXTURE, getX(), getY(), getWidth(), getHeight());
                ((SortButtonState) onPress).renderTooltip(context, mouseX, mouseY);
            } else context.blitSprite(TEXTURE, getX(), getY(), getWidth(), getHeight());
        }
    }

    private class WidgetButtonPage extends Button {
        private static final int WIDTH = 120;
        private static final int HEIGHT = 22;

        static final ResourceLocation TEXTURE = MusicalFeature.id("container/disc_press/button");
        static final ResourceLocation HIGHLIGHTED_TEXTURE = MusicalFeature.id("container/disc_press/button_highlighted");
        static final ResourceLocation SELECTED_TEXTURE = MusicalFeature.id("container/disc_press/button_selected");

        static final int METADATA_COLOR = 0xFF685E4A;
        static final int METADATA_HOVERED_COLOR = 0xFF51493A;
        static final int METADATA_SELECTED_COLOR = 0xFFA09172;
        static final int HIGHLIGHT_TITLE_TEXT_COLOR = 0xFFFFFF80;

        final int index;

        WidgetButtonPage(int x, int y, int index, Button.OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.index = index;
            this.visible = false;
        }

        protected @Override void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
            var m = indexStartOffset + index;
            if (m >= cache.size()) return;

            context.setColor(1.0F, 1.0F, 1.0F, alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();

            context.blitSprite(getTexture(), getX(), getY(), getWidth(), getHeight());
            context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            renderText(context, m);
        }

        private void renderText(GuiGraphics context, int m) {
            var isSelected = selectedId == cache.idOf(indexStartOffset + index);

            var titleColor = isHovered() || isSelected ? HIGHLIGHT_TITLE_TEXT_COLOR : CommonColors.WHITE;
            var metadataColor = isSelected
                    ? METADATA_SELECTED_COLOR
                    : (isHovered() ? METADATA_HOVERED_COLOR : METADATA_COLOR);

            var title = cache.getTitle(m);
            var artist = cache.getArtist(m);
            var duration = cache.getDuration(m);

            context.drawString(font, title, getX() + WIDGET_OFFSET_X, getY() + WIDGET_OFFSET_Y, titleColor, true);
            context.drawString(font, artist, getX() + WIDGET_OFFSET_X, getY() + WIDGET_OFFSET_Y + font.lineHeight, metadataColor, false);
            context.drawString(font, duration, getX() + WIDTH + WIDGET_DURATION_OFFSET_X, getY() + WIDGET_OFFSET_Y + (font.lineHeight >> 1), metadataColor, false);
        }

        private void renderTooltip(GuiGraphics context, int x, int y) {
            var m = index + indexStartOffset;
            if (!isHovered || m >= cache.size()) return;
            context.renderComponentTooltip(
                font,
                cache.getTooltip(m),
                x, y
            );
        }

        private ResourceLocation getTexture() {
            if (!active) return SELECTED_TEXTURE;
            if (selectedId == cache.idOf(indexStartOffset + index)) return SELECTED_TEXTURE;
            return isHovered() ? HIGHLIGHTED_TEXTURE: TEXTURE;
        }
    }

    private static class Cache {
        private static final int MAX_TEXT_WIDTH = 80;
        private static final int MAX_TRUNCATED_WIDTH = MAX_TEXT_WIDTH - Minecraft.getInstance().font.width("...");
        private static final Component UNKNOWN_ARTIST = Component.literal("Unknown");

        private final List<Holder<JukeboxSong>> entries;

        private final Component[] titles;
        private final Component[] artists;
        private final Component[] durations;
        private final List<Component>[] tooltips;

        private final int[] indexMappings;
        private final int[] filteredIndexMappings;
        private int filteredMappingsSize;

        @SuppressWarnings("unchecked")
        Cache(List<Holder<JukeboxSong>> songEntries) {
            int size = songEntries.size();
            entries = songEntries;

            tooltips = (List<Component>[]) new List<?>[size];
            titles = new Component[size];
            artists = new Component[size];
            durations = new Component[size];
            loadAllTexts();

            indexMappings = createIndexMappings(size);
            initSort();
            filteredIndexMappings = indexMappings.clone();
            filteredMappingsSize = filteredIndexMappings.length;
        }

        private static int[] createIndexMappings(int size) {
            var mappings = new int[size];
            for (int i = 0; i < size; i++) mappings[i] = i;
            return mappings;
        }

        void updateFilter(String filter) {
            if (filter.isBlank()) { resetFilter(); return; }

            var artists = this.artists;
            var titles = this.titles;

            var mappings = indexMappings;

            int r = 0; int w = 0;

            while (r < mappings.length) {
                var id = mappings[r];
                boolean matches;

                var title = titles[id].tryCollapseToString();
                matches = title != null && StringUtil.containsInsensitive(title, filter);
                if (!matches) {
                    var artist = artists[id].tryCollapseToString();
                    matches = artist != null && StringUtil.containsInsensitive(artist, filter);
                }
                if (matches) { filteredIndexMappings[w++] = id; }
                r++;
            }
            filteredMappingsSize = w;
        }

        private void resetFilter() {
            System.arraycopy(
                    indexMappings, 0,
                    filteredIndexMappings, 0,
                    Math.min(indexMappings.length, filteredIndexMappings.length)
            );
            filteredMappingsSize = filteredIndexMappings.length;
        }

        private void initSort() {
            IntComparator nameSorter = (a, b) -> {
                var firstString = titles[a].tryCollapseToString();
                var secondString = titles[b].tryCollapseToString();

                if (firstString == null) return secondString == null ? 0 : -1;
                if (secondString == null) return 1;

                return String.CASE_INSENSITIVE_ORDER.compare(firstString, secondString);
            };
            IntArrays.stableSort(indexMappings, nameSorter);
        }

        void sortByAscendingTitle(String filter) {
            initSort();
            updateFilter(filter);
        }

        void sortByDescendingTitle(String filter) {
            IntComparator nameSorter = (a, b) -> {
                var firstString = titles[a].tryCollapseToString();
                var secondString = titles[b].tryCollapseToString();

                if (firstString == null) return secondString == null ? 0 : 1;
                if (secondString == null) return -1;

                return String.CASE_INSENSITIVE_ORDER.compare(secondString, firstString);
            };
            IntArrays.stableSort(indexMappings, nameSorter);
            updateFilter(filter);
        }

        void sortByAscendingArtist(String filter) {
            IntComparator nameSorter = (a, b) -> {
                var firstString = artists[a].tryCollapseToString();
                var secondString = artists[b].tryCollapseToString();

                if (firstString == null) return secondString == null ? 0 : -1;
                if (secondString == null) return 1;

                return String.CASE_INSENSITIVE_ORDER.compare(firstString, secondString);
            };
            IntArrays.stableSort(indexMappings, nameSorter);
            updateFilter(filter);
        }

        void sortByDescendingArtist(String filter) {
            IntComparator nameSorter = (a, b) -> {
                var firstString = artists[a].tryCollapseToString();
                var secondString = artists[b].tryCollapseToString();

                if (firstString == null) return secondString == null ? 0 : 1;
                if (secondString == null) return -1;

                return String.CASE_INSENSITIVE_ORDER.compare(secondString, firstString);
            };
            IntArrays.stableSort(indexMappings, nameSorter);
            updateFilter(filter);
        }

        void sortByAscendingDuration(String filter) {
            IntComparator durationSorter =(a, b) -> Float.compare(
                    entries.get(a).value().lengthInSeconds(),
                    entries.get(b).value().lengthInSeconds()
            );
            IntArrays.stableSort(indexMappings, durationSorter);
            updateFilter(filter);
        }

        void sortByDescendingDuration(String filter) {
            IntComparator durationSorter = (a, b) -> Float.compare(
                    entries.get(b).value().lengthInSeconds(),
                    entries.get(a).value().lengthInSeconds()
            );
            IntArrays.stableSort(indexMappings, durationSorter);
            updateFilter(filter);
        }

        int size() { return filteredMappingsSize; }
        boolean isEmpty() { return entries.isEmpty(); }

        int idOf(int index) { return filteredIndexMappings[index]; }

        Component getTitle(int index) { return titles[idOf(index)]; }
        Component getArtist(int index) { return artists[idOf(index)]; }
        Component getDuration(int index) { return durations[idOf(index)]; }
        List<Component> getTooltip(int index) { return tooltips[idOf(index)]; }

        private void loadAllTexts() { for (int i = 0; i < entries.size(); i++) loadTextsAt(i); }
        private void loadTextsAt(int id) {
            var song = entries.get(id).value();

            var duration = Component.literal(net.minecraft.util.StringUtil.formatTickDuration(song.lengthInTicks(), 20.0F));
            var description = song.description();
            var tooltip = List.of(description, duration.copy().withColor(CommonColors.LIGHT_GRAY));

            var fullTitle = description.getString();
            int delimIndex = fullTitle.indexOf('-');
            if (delimIndex < 0) {
                setTexts(id, entryTextOf(fullTitle), UNKNOWN_ARTIST, duration, tooltip);
                return;
            }

            var i = delimIndex + 1;
            while (i < fullTitle.length() && Character.isWhitespace(fullTitle.charAt(i))) i++;

            var j = delimIndex - 1;
            while (j > 0 && Character.isWhitespace(fullTitle.charAt(j))) j--;

            setTexts(
                id,
                entryTextOf(fullTitle.substring(i)),
                entryTextOf(fullTitle.substring(0, j + 1)),
                duration,
                tooltip
            );
        }

        private void setTexts(int id, Component title, Component artist, Component duration, List<Component> tooltip) {
            titles[id] = title;
            artists[id] = artist;
            durations[id] = duration;
            tooltips[id] = tooltip;
        }

        private static Component entryTextOf(String string) {
            var textRenderer = Minecraft.getInstance().font;

            if (textRenderer.width(string) < MAX_TEXT_WIDTH) return Component.literal(string);
            var truncatedString = textRenderer.plainSubstrByWidth(string, MAX_TRUNCATED_WIDTH) + "...";
            return Component.literal(truncatedString);
        }
    }

    private enum SortType {
        TITLE_ASCENDING("Title (Ascending)") {
            @Override void sort(Cache cache, String filter) { cache.sortByAscendingTitle(filter); }
            @Override SortType next() { return TITLE_DESCENDING; }
        },
        TITLE_DESCENDING("Title (Descending)") {
            @Override void sort(Cache cache, String filter) { cache.sortByDescendingTitle(filter); }
            @Override SortType next() { return ARTIST_ASCENDING; }
        },
        ARTIST_ASCENDING("Artist (Ascending)") {
            @Override void sort(Cache cache, String filter) { cache.sortByAscendingArtist(filter); }
            @Override SortType next() { return ARTIST_DESCENDING; }
        },
        ARTIST_DESCENDING("Artist (Descending)") {
            @Override void sort(Cache cache, String filter) { cache.sortByDescendingArtist(filter); }
            @Override SortType next() { return DURATION_ASCENDING; }
        },
        DURATION_ASCENDING("Duration (Ascending)") {
            @Override void sort(Cache cache, String filter) { cache.sortByAscendingDuration(filter); }
            @Override SortType next() { return DURATION_DESCENDING; }
        },
        DURATION_DESCENDING("Duration (Descending)") {
            @Override void sort(Cache cache, String filter) { cache.sortByDescendingDuration(filter); }
            @Override SortType next() { return TITLE_ASCENDING; }
        };

        final List<Component> tooltip;

        SortType(String tooltipEnd) {
            var tooltipText = "Sorted by: " + tooltipEnd;
            tooltip = List.of(Component.literal(tooltipText));
        }

        abstract void sort(Cache cache, String filter);
        abstract SortType next();
    }
}
