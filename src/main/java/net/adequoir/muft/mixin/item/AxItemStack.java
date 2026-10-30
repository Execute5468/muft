package net.adequoir.muft.mixin.item;

import net.minecraft.component.ComponentMapImpl;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemStack.class)
public interface AxItemStack {
    @Invoker("<init>")
    static ItemStack axInit(ItemConvertible item, int count, ComponentMapImpl components) { throw new AssertionError(); }
}
