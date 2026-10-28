package net.adequoir.muft.mixin.client.gui.render.model;

import net.adequoir.muft.client.render.item.PocketJukeboxRenderer;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelLoader.class)
public abstract class MxModelLoader {
    @Shadow
    protected abstract void loadItemModel(ModelIdentifier id);

    @Inject(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
            ordinal = 1,
            shift = At.Shift.AFTER
        )
    )
    private void onRegisterSpecial(
        BlockColors blockColors,
        Profiler profiler,
        Map<Identifier, JsonUnbakedModel> jsonUnbakedModels,
        Map<Identifier, List<BlockStatesLoader.SourceTrackedData>> blockStates, CallbackInfo ci
    ) {
        PocketJukeboxRenderer.forEachModelId((front, back) -> {
            loadItemModel(front);
            loadItemModel(back);
        });
    }
}
