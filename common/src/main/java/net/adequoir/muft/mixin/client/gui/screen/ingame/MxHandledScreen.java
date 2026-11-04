package net.adequoir.muft.mixin.client.gui.screen.ingame;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.adequoir.muft.client.gui.tooltip.PocketJukeboxTooltipComponent;
import net.adequoir.muft.client.gui.tooltip.PocketJukeboxTooltipSubmenu;
import net.adequoir.muft.client.gui.tooltip.TooltipSubmenu;
import net.adequoir.muft.mixin.client.gui.screen.AxScreen;
import net.adequoir.muft.mixutils.client.gui.screen.ingame.IHandledScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
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
import java.util.Optional;

@Mixin(AbstractContainerScreen.class)
public abstract class MxHandledScreen implements IHandledScreen {
    @Shadow protected @Nullable Slot hoveredSlot;

    @Shadow protected @Final AbstractContainerMenu menu;
    @Unique private final List<TooltipSubmenu> tooltipSubmenus = new ArrayList<>();
    @Unique private @Nullable Slot lastFocusedSlot;

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        tooltipSubmenus.clear();
        var client = ((AxScreen) this).axClient();
        muft_addTooltipSubmenu(new PocketJukeboxTooltipSubmenu(client));
    }

    public @Override void muft_addTooltipSubmenu(TooltipSubmenu submenu) { tooltipSubmenus.add(submenu); }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        lastFocusedSlot = this.hoveredSlot;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderTail(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (lastFocusedSlot != null && lastFocusedSlot != hoveredSlot) {
            resetTooltipSubmenus(lastFocusedSlot);
        }
        lastFocusedSlot = null;
    }

    public @Override boolean muft_mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (hoveredSlot == null || !hoveredSlot.hasItem()) return false;
        for (int i = 0; i < tooltipSubmenus.size(); i++) {
            var submenu = tooltipSubmenus.get(i);
            if (!submenu.isApplicableTo(hoveredSlot)) continue;
            if (!submenu.onScroll(horizontalAmount, verticalAmount, hoveredSlot.index, hoveredSlot.getItem())) continue;
            return true;
        }
        return false;
    }

    @Unique
    private void resetTooltipSubmenus(Slot slot) {
        if (!slot.hasItem()) return;
        for (int i = 0; i < tooltipSubmenus.size(); i++) {
            var submenu = tooltipSubmenus.get(i);
            if (submenu.isApplicableTo(slot)) submenu.reset(slot);
        }
    }

    @Inject(
        method = "slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleInventoryMouseClick(IIILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V"
        )
    )
    private void whenMouseClick(@Nullable Slot slot, int slotId, int button, ClickType actionType, CallbackInfo ci) {
        if (slot == null || !slot.hasItem()) return;
        for (int i = 0; i < tooltipSubmenus.size(); i++) {
            var submenu = tooltipSubmenus.get(i);
            if (submenu.isApplicableTo(slot)) submenu.onMouseClick(slot, actionType);
        }
    }

    @Inject(
        method = "onClose",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;onClose()V"
        )
    )
    private void onClose(CallbackInfo ci) { if (hoveredSlot != null) resetTooltipSubmenus(hoveredSlot); }

    @Redirect(
        method = "renderTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
            ordinal = 0
        )
    )
    private boolean mouseoverHandlerCursorStackRedirect(ItemStack instance) { return true; }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @WrapOperation(
        method = "renderTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"
        ),
        require = 0
    )
    private void mouseoverDrawRedirect(
        GuiGraphics context, Font font, List<Component> tooltipLines, Optional<TooltipComponent> visualTooltipComponent, int mouseX, int mouseY, Operation<Void> original,
        @Local ItemStack stack
    ) {
        if (!menu.getCarried().isEmpty() && !isItemTooltipSticky(stack)) return;
        original.call(context, font, tooltipLines, visualTooltipComponent, mouseX, mouseY);
    }

    @Unique
    private boolean isItemTooltipSticky(ItemStack item) {
        var data = item.getTooltipImage().orElse(null);
        return data instanceof PocketJukeboxTooltipComponent;
    }
}
