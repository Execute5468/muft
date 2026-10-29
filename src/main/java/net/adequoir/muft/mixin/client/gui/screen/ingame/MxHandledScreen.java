package net.adequoir.muft.mixin.client.gui.screen.ingame;

import com.llamalad7.mixinextras.sugar.Local;
import net.adequoir.muft.client.gui.tooltip.PocketJukeboxTooltipComponent;
import net.adequoir.muft.client.gui.tooltip.PocketJukeboxTooltipSubmenu;
import net.adequoir.muft.client.gui.tooltip.TooltipSubmenu;
import net.adequoir.muft.mixutils.client.gui.screen.ingame.IHandledScreen;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mixin(HandledScreen.class)
public abstract class MxHandledScreen extends Screen implements IHandledScreen {
    @Shadow protected @Nullable Slot focusedSlot;

    @Shadow protected @Final ScreenHandler handler;
    @Unique private final List<TooltipSubmenu> tooltipSubmenus = new ArrayList<>();
    @Unique private @Nullable Slot lastFocusedSlot;

    protected MxHandledScreen(Text title) { super(title); }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        tooltipSubmenus.clear();
        Objects.requireNonNull(this.client);
        muft_addTooltipSubmenu(new PocketJukeboxTooltipSubmenu(this.client));
    }

    public @Override void muft_addTooltipSubmenu(TooltipSubmenu submenu) { tooltipSubmenus.add(submenu); }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        lastFocusedSlot = this.focusedSlot;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderTail(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (lastFocusedSlot != null && lastFocusedSlot != focusedSlot) {
            resetTooltipSubmenus(lastFocusedSlot);
        }
        lastFocusedSlot = null;
    }

    public @Override boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (focusedSlot == null || !focusedSlot.hasStack()) return false;
        for (int i = 0; i < tooltipSubmenus.size(); i++) {
            var submenu = tooltipSubmenus.get(i);
            if (!submenu.isApplicableTo(focusedSlot)) continue;
            if (!submenu.onScroll(horizontalAmount, verticalAmount, focusedSlot.id, focusedSlot.getStack())) continue;
            return true;
        }
        return false;
    }

    @Unique
    private void resetTooltipSubmenus(Slot slot) {
        if (!slot.hasStack()) return;
        for (int i = 0; i < tooltipSubmenus.size(); i++) {
            var submenu = tooltipSubmenus.get(i);
            if (submenu.isApplicableTo(slot)) submenu.reset(slot);
        }
    }

    @Inject(
        method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;clickSlot(IIILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"
        )
    )
    private void whenMouseClick(@Nullable Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (slot == null || !slot.hasStack()) return;
        for (int i = 0; i < tooltipSubmenus.size(); i++) {
            var submenu = tooltipSubmenus.get(i);
            if (submenu.isApplicableTo(slot)) submenu.onMouseClick(slot, actionType);
        }
    }

    @Inject(
        method = "close",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/Screen;close()V"
        )
    )
    private void onClose(CallbackInfo ci) { if (focusedSlot != null) resetTooltipSubmenus(focusedSlot); }

    @Redirect(
        method = "drawMouseoverTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
            ordinal = 0
        )
    )
    private boolean mouseoverHandlerCursorStackRedirect(ItemStack instance) { return true; }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Redirect(
        method = "drawMouseoverTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"
        )
    )
    private void mouseoverDrawRedirect(
        DrawContext context, TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y,
        @Local ItemStack stack
    ) {
        if (!handler.getCursorStack().isEmpty() && !isItemTooltipSticky(stack)) return;
        context.drawTooltip(textRenderer, text, data, x, y);
    }

    @Unique
    private boolean isItemTooltipSticky(ItemStack item) {
        var data = item.getTooltipData().orElse(null);
        return data instanceof PocketJukeboxTooltipComponent;
    }
}
