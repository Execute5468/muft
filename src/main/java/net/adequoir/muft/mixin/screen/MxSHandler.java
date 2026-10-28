package net.adequoir.muft.mixin.screen;

import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.mixutils.screen.MuftSHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ScreenHandler.class)
public class MxSHandler implements MuftSHandler {
    @Shadow public @Final DefaultedList<Slot> slots;

    @Override
    public void muft_selectPocketJukeboxStack(int slotId, int stackIndex) {
        if (slotId < 0 || slotId >= slots.size()) return;
        var stack = slots.get(slotId).getStack();
        var newComponents = PocketJukeboxComponent.get(stack).withSelected(stackIndex);
        PocketJukeboxComponent.set(stack, newComponents);
    }
}
