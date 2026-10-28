package net.adequoir.muft.mixin.client.render.item;

import net.adequoir.muft.client.render.item.PocketJukeboxRenderer;
import net.adequoir.muft.item.PocketJukeboxItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MxItemRenderer {
    @Shadow private @Final ItemModels models;

    @Inject(
        method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
        at = @At("HEAD"),
        order = 1,
        cancellable = true
    )
    private void onRenderItem(
        ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded,
        MatrixStack matrices, VertexConsumerProvider vertexConsumers,
        int light, int overlay, BakedModel model,
        CallbackInfo ci
    ) {
        if (!(stack.getItem() instanceof PocketJukeboxItem)) return;
        boolean bl = PocketJukeboxRenderer.render(
            stack, renderMode, leftHanded,
            matrices, vertexConsumers,
            light, overlay,
            (ItemRenderer) (Object) this, models
        );
        if (bl) ci.cancel();
    }
}
