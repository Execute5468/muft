package net.adequoir.muft.client.render.item;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.component.PocketJukeboxComponent;
import net.adequoir.muft.item.PocketJukeboxItem;
import net.adequoir.muft.mixin.client.render.item.AxItemRenderer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static net.adequoir.muft.MusicalFeature.LOGGER;

public final class PocketJukeboxRenderer {
    private static final Map<Item, JukeboxModelPair> modelPairs = new IdentityHashMap<>();

    static {
        LOGGER.info("Loading {} Pocket Jukebox model pairs", PocketJukeboxItem.POCKET_JUKEBOXES.size());
        PocketJukeboxItem.POCKET_JUKEBOXES.forEach(i -> modelPairs.computeIfAbsent(
            i,
            j -> {
                var itemId = Registries.ITEM.getId(i);
                var frontId = itemId.withSuffixedPath("_open_front");
                var backId = itemId.withSuffixedPath("_open_back");

                return new JukeboxModelPair(ModelIdentifier.ofInventoryVariant(frontId), ModelIdentifier.ofInventoryVariant(backId));
            }
        ));
    }

    public static void forEachModelId(BiConsumer<ModelIdentifier, ModelIdentifier> consumer) {
        for (var modelPair: modelPairs.values()) consumer.accept(modelPair.front, modelPair.back);
    }

    public static boolean render(
        ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded,
        MatrixStack matrices, VertexConsumerProvider vertexProvider,
        int light, int overlay,
        ItemRenderer renderer, ItemModels models
    ) {
        if (renderMode != ModelTransformationMode.GUI) return false;
        var component = PocketJukeboxComponent.get(stack);
        if (!component.hasSelected()) return false;
        var modelPair = modelPairs.get(stack.getItem());
        if (modelPair == null) return false;
        var backModel = models.getModelManager().getModel(modelPair.back);
        var frontModel = models.getModelManager().getModel(modelPair.front);
        renderModel(matrices, vertexProvider, stack, renderMode, leftHanded, backModel, renderer, light, overlay);

        var selectedStack = PocketJukeboxComponent._unsafe_getSelected(component);
        renderer.renderItem(selectedStack, renderMode, light, overlay, matrices, vertexProvider, null, 0);

        renderModel(matrices, vertexProvider, stack, renderMode, leftHanded, frontModel, renderer, light, overlay);
        return true;
    }

    private static void renderModel(
        MatrixStack matrices,VertexConsumerProvider provider,
        ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, BakedModel model,
        ItemRenderer renderer, int light, int overlay
    ) {
        matrices.push();
        model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);
        matrices.translate(-0.5F, -0.5F, -0.5F);

        var renderLayer = RenderLayers.getItemLayer(stack, true);
        var vertices = ItemRenderer.getDirectItemGlintConsumer(provider, renderLayer, true, stack.hasGlint());
        ((AxItemRenderer) renderer).axRenderBakedItemModel(model, stack, light, overlay, matrices, vertices);
        matrices.pop();
    }

    private record JukeboxModelPair(ModelIdentifier front, ModelIdentifier back) {}

    private PocketJukeboxRenderer() {}
}
