package net.adequoir.muft.mixin.client.gui.tooltip;

import net.adequoir.muft.client.gui.tooltip.PocketJukeboxTooltipComponent;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientTooltipComponent.class)
public interface MxTooltipComponent {
    @Inject(
        method = "create(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void onOf(TooltipComponent data, CallbackInfoReturnable<ClientTooltipComponent> cir) {
        if (!(data instanceof PocketJukeboxComponent jukeboxComponent)) return;
        cir.cancel();
        cir.setReturnValue(new PocketJukeboxTooltipComponent(jukeboxComponent));
    }
}
