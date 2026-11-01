package net.adequoir.muft.mixin.client.entity.player;

import net.adequoir.muft.client.sound.PocketJukeboxManager;
import net.adequoir.muft.mixutils.client.entity.player.IClientPE;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientPlayerEntity.class)
public class MxClientPE implements IClientPE {
    @Shadow private @Final List<ClientPlayerTickable> tickables;

    @Unique private final PocketJukeboxManager pocketJukeboxManager = new PocketJukeboxManager((ClientPlayerEntity) (Object) this);

    public @Override PocketJukeboxManager muft_pocketJukeboxTicker() { return pocketJukeboxManager; }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(
        MinecraftClient client,
        ClientWorld world,
        ClientPlayNetworkHandler networkHandler,
        StatHandler stats,
        ClientRecipeBook recipeBook,
        boolean lastSneaking,
        boolean lastSprinting,
        CallbackInfo ci
    ) { tickables.add(pocketJukeboxManager); }
}