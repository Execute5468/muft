package net.adequoir.muft.mixin.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Screen.class)
public interface AxScreen {
    @Accessor("minecraft") Minecraft axClient();
}
