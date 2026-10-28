package net.adequoir.muft.mixin.client.gui.tooltip;

import net.adequoir.muft.client.gui.tooltip.PocketJukeboxTooltipComponent;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TooltipComponent.class)
public interface MxTooltipComponent {
    @Inject(
        method = "of(Lnet/minecraft/item/tooltip/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void onOf(TooltipData data, CallbackInfoReturnable<TooltipComponent> cir) {
        if (!(data instanceof PocketJukeboxComponent jukeboxComponent)) return;
        cir.cancel();
        cir.setReturnValue(new PocketJukeboxTooltipComponent(jukeboxComponent));
    }
}
