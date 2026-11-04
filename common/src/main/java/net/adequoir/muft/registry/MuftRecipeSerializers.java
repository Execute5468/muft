package net.adequoir.muft.registry;

import net.adequoir.muft.Muft;
import net.adequoir.muft.recipe.PocketJukeboxColoringRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.adequoir.muft.api.registry.Registrar;
import net.adequoir.muft.api.registry.Registrars;
import java.util.function.Supplier;

public sealed interface MuftRecipeSerializers permits RegistryLoader {
    Registrar<RecipeSerializer<?>> REGISTRAR = Registrars.create(Muft.MOD_ID, BuiltInRegistries.RECIPE_SERIALIZER);

    Supplier<RecipeSerializer<PocketJukeboxColoringRecipe>> POCKET_JUKEBOX_COLORING = REGISTRAR.register(
        "crafting_special_pocket_jukebox_coloring",
        new SimpleCraftingRecipeSerializer<>(PocketJukeboxColoringRecipe::new)
    );

    static void init() {}
}
