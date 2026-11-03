package net.adequoir.muft.recipe;

import net.adequoir.muft.item.PocketJukeboxItem;
import net.adequoir.muft.registry.MuftRecipeSerializers;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class PocketJukeboxColoringRecipe extends SpecialCraftingRecipe {
    public PocketJukeboxColoringRecipe(CraftingRecipeCategory category) { super(category); }


    public @Override boolean matches(CraftingRecipeInput input, World world) {
        boolean hasPocketJukebox = false;
        boolean hasDye = false;

        for (int i = 0; i < input.getSize(); i++) {
            var stack = input.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof PocketJukeboxItem) {
                if (hasPocketJukebox) return false;
                hasPocketJukebox = true;
            } else if (stack.getItem() instanceof DyeItem) {
                if (hasDye) return false;
                hasDye = true;
            } else return false;
        }
        return hasPocketJukebox && hasDye;
    }

    public @Override ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        var stack = ItemStack.EMPTY;
        DyeItem dye = null;

        for (int i = 0; i < input.getSize(); i++) {
            var slotStack = input.getStackInSlot(i);
            if (slotStack.isEmpty()) continue;
            var slotItem = slotStack.getItem();
            if (slotItem instanceof PocketJukeboxItem) {
                stack = slotStack;
            } else if (slotItem instanceof DyeItem dyeItem) {
                dye = dyeItem;
            }
        }

        var dyedItem = PocketJukeboxItem.get(dye == null ? null : dye.getColor());
        return stack.copyComponentsToNewStack(dyedItem, 1);
    }

    public @Override boolean fits(int width, int height) { return width * height >= 2; }
    public @Override RecipeSerializer<?> getSerializer() { return MuftRecipeSerializers.POCKET_JUKEBOX_COLORING.get(); }
}
