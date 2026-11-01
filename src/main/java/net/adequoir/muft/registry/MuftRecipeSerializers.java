package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.recipe.PocketJukeboxColoringRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class MuftRecipeSerializers {
    public static final RecipeSerializer<PocketJukeboxColoringRecipe> POCKET_JUKEBOX = register(
        "crafting_special_pocket_jukebox_coloring",
        new SpecialRecipeSerializer<>(PocketJukeboxColoringRecipe::new)
    );

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, MusicalFeature.id(id), serializer);
    }

    public static void init() {}

    private MuftRecipeSerializers() {}
}
