package net.adequoir.muft.mixin.screen;

import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.mixutils.screen.MuftSHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerMenu.class)
public class MxSHandler implements MuftSHandler {
    @Shadow public @Final NonNullList<Slot> slots;

    @Override
    public void muft_selectPocketJukeboxStack(int slotId, int stackIndex) {
        if (slotId < 0 || slotId >= slots.size()) return;
        var stack = slots.get(slotId).getItem();
        var newComponents = PocketJukeboxComponent.get(stack).withSelected(stackIndex);
        PocketJukeboxComponent.set(stack, newComponents);
    }
}
