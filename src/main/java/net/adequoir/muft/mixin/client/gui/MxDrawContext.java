package net.adequoir.muft.mixin.client.gui;

import com.llamalad7.mixinextras.sugar.Local;
import net.adequoir.muft.client.gui.tooltip.MuftTooltipComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiGraphics.class)
public class MxDrawContext {
    @Redirect(
        method = "renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;renderImage(Lnet/minecraft/client/gui/Font;IILnet/minecraft/client/gui/GuiGraphics;)V"
        )
    )
    private void onDrawTooltipItems(
        ClientTooltipComponent instance, Font textRenderer, int x, int y, GuiGraphics context,
        @Local(ordinal = 4) int l, @Local(ordinal = 5) int m
    ) {
        if (instance instanceof MuftTooltipComponent muftTooltipComponent) {
            muftTooltipComponent.drawItems(textRenderer, x, y, l, m, context);
        } else instance.renderImage(textRenderer, x, y, context);
    }
}
