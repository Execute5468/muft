package net.adequoir.muft.mixin.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.adequoir.muft.client.render.item.PocketJukeboxRenderer;
import net.adequoir.muft.item.PocketJukeboxItem;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MxItemRenderer {
    @Shadow private @Final ItemModelShaper itemModelShaper;

    @Inject(
        method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
        at = @At("HEAD"),
        order = 1,
        cancellable = true
    )
    private void onRenderItem(
        ItemStack stack, ItemDisplayContext renderMode, boolean leftHanded,
        PoseStack matrices, MultiBufferSource vertexConsumers,
        int light, int overlay, BakedModel model,
        CallbackInfo ci
    ) {
        if (!(stack.getItem() instanceof PocketJukeboxItem)) return;
        boolean bl = PocketJukeboxRenderer.render(
            stack, renderMode, leftHanded,
            matrices, vertexConsumers,
            light, overlay,
            (ItemRenderer) (Object) this, itemModelShaper
        );
        if (bl) ci.cancel();
    }
}
