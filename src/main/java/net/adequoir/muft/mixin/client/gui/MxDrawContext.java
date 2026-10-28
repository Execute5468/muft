package net.adequoir.muft.mixin.client.gui;

import com.llamalad7.mixinextras.sugar.Local;
import net.adequoir.muft.client.gui.tooltip.MuftTooltipComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DrawContext.class)
public class MxDrawContext {
    @Redirect(
        method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;drawItems(Lnet/minecraft/client/font/TextRenderer;IILnet/minecraft/client/gui/DrawContext;)V"
        )
    )
    private void onDrawTooltipItems(
        TooltipComponent instance, TextRenderer textRenderer, int x, int y, DrawContext context,
        @Local(ordinal = 4) int l, @Local(ordinal = 5) int m
    ) {
        if (instance instanceof MuftTooltipComponent muftTooltipComponent) {
            muftTooltipComponent.drawItems(textRenderer, x, y, l, m, context);
        } else instance.drawItems(textRenderer, x, y, context);
    }
}
