package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.recipe.PocketJukeboxColoringRecipe;
import net.adequoir.muft.api.registry.Registrar;
import net.adequoir.muft.api.registry.Registrars;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;

import java.util.function.Supplier;

public sealed interface MuftRecipeSerializers permits RegistryLoader {
    Registrar<RecipeSerializer<?>> REGISTRAR = Registrars.create(MusicalFeature.MOD_ID, Registries.RECIPE_SERIALIZER);

    Supplier<RecipeSerializer<PocketJukeboxColoringRecipe>> POCKET_JUKEBOX_COLORING = REGISTRAR.register(
        "crafting_special_pocket_jukebox_coloring",
        new SpecialRecipeSerializer<>(PocketJukeboxColoringRecipe::new)
    );

    static void init() {}
}
