package net.adequoir.muft.mixin.client.entity.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

@Mixin(Inventory.class)
public interface AxPlayerInventory {
    @Accessor("compartments")
    List<NonNullList<ItemStack>> axCombinedInventory();
}
