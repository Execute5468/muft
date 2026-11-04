package net.adequoir.muft.mixin.client.gui.render.model;

import net.adequoir.muft.client.render.item.PocketJukeboxRenderer;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class MxModelLoader {
    @Shadow
    protected abstract void loadSpecialItemModelAndDependencies(ModelResourceLocation id);

    @Inject(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
            ordinal = 1,
            shift = At.Shift.AFTER
        )
    )
    private void onRegisterSpecial(
        BlockColors blockColors,
        ProfilerFiller profiler,
        Map<ResourceLocation, BlockModel> jsonUnbakedModels,
        Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStates, CallbackInfo ci
    ) {
        PocketJukeboxRenderer.forEachModelId((front, back) -> {
            loadSpecialItemModelAndDependencies(front);
            loadSpecialItemModelAndDependencies(back);
        });
    }
}
