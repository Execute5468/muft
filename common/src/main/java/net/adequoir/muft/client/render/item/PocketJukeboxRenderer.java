package net.adequoir.muft.client.render.item;

import net.adequoir.muft.client.util.RenderHelper;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.item.PocketJukeboxItem;
import net.adequoir.muft.mixin.client.render.item.AxItemRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static net.adequoir.muft.Muft.LOGGER;

import com.mojang.blaze3d.vertex.PoseStack;

public final class PocketJukeboxRenderer {
    private static final Map<Item, JukeboxModelPair> modelPairs = new IdentityHashMap<>();

    static {
        LOGGER.info("Loading {} Pocket Jukebox model pairs", PocketJukeboxItem.POCKET_JUKEBOXES.size());
        PocketJukeboxItem.POCKET_JUKEBOXES.forEach(i -> modelPairs.computeIfAbsent(
            i,
            j -> {
                var itemId = BuiltInRegistries.ITEM.getKey(i);
                var frontId = itemId.withSuffix("_open_front");
                var backId = itemId.withSuffix("_open_back");

                return new JukeboxModelPair(ModelResourceLocation.inventory(frontId), ModelResourceLocation.inventory(backId));
            }
        ));
    }

    public static void forEachModelId(BiConsumer<ModelResourceLocation, ModelResourceLocation> consumer) {
        for (var modelPair: modelPairs.values()) consumer.accept(modelPair.front, modelPair.back);
    }

    public static boolean render(
        ItemStack stack, ItemDisplayContext renderMode, boolean leftHanded,
        PoseStack matrices, MultiBufferSource vertexProvider,
        int light, int overlay,
        ItemRenderer renderer, ItemModelShaper models
    ) {
        if (renderMode != ItemDisplayContext.GUI) return false;
        var component = PocketJukeboxComponent.get(stack);
        if (!component.hasSelected()) return false;
        var modelPair = modelPairs.get(stack.getItem());
        if (modelPair == null) return false;
        var backModel = models.getModelManager().getModel(modelPair.back);
        var frontModel = models.getModelManager().getModel(modelPair.front);
        renderModel(matrices, vertexProvider, stack, renderMode, leftHanded, backModel, renderer, light, overlay);

        var selectedStack = component.getSelected();
        RenderHelper.renderItem(renderer, selectedStack, renderMode, light, overlay, matrices, vertexProvider, null, 0);

        renderModel(matrices, vertexProvider, stack, renderMode, leftHanded, frontModel, renderer, light, overlay);
        return true;
    }

    private static void renderModel(
        PoseStack matrices,MultiBufferSource provider,
        ItemStack stack, ItemDisplayContext renderMode, boolean leftHanded, BakedModel model,
        ItemRenderer renderer, int light, int overlay
    ) {
        matrices.pushPose();
        model.getTransforms().getTransform(renderMode).apply(leftHanded, matrices);
        matrices.translate(-0.5F, -0.5F, -0.5F);

        var renderLayer = ItemBlockRenderTypes.getRenderType(stack, true);
        var vertices = ItemRenderer.getFoilBufferDirect(provider, renderLayer, true, stack.hasFoil());
        ((AxItemRenderer) renderer).axRenderBakedItemModel(model, stack, light, overlay, matrices, vertices);
        matrices.popPose();
    }

    private record JukeboxModelPair(ModelResourceLocation front, ModelResourceLocation back) {}

    private PocketJukeboxRenderer() {}
}
