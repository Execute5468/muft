package net.adequoir.muft.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.adequoir.muft.mixutils.client.gui.screen.ingame.IHandledScreen;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public class MxMouse {
    @WrapOperation(
        method = "onScroll",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDDD)Z"
        )
    )
    private boolean wrapScreenMouseScroll(Screen instance, double v, double v1, double v2, double v3, Operation<Boolean> operation) {
        if (instance instanceof IHandledScreen iHandledScreen && iHandledScreen.muft_mouseScrolled(v, v1, v2, v3)) return true;
        return operation.call(instance, v, v1, v2, v3);
    }
}
