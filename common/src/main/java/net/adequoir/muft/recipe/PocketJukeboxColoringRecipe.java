package net.adequoir.muft.recipe;

import net.adequoir.muft.item.PocketJukeboxItem;
import net.adequoir.muft.registry.MuftRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class PocketJukeboxColoringRecipe extends CustomRecipe {
    public PocketJukeboxColoringRecipe(CraftingBookCategory category) { super(category); }


    public @Override boolean matches(CraftingInput input, Level world) {
        boolean hasPocketJukebox = false;
        boolean hasDye = false;

        for (int i = 0; i < input.size(); i++) {
            var stack = input.getItem(i);
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

    public @Override ItemStack assemble(CraftingInput input, HolderLookup.Provider lookup) {
        var stack = ItemStack.EMPTY;
        DyeItem dye = null;

        for (int i = 0; i < input.size(); i++) {
            var slotStack = input.getItem(i);
            if (slotStack.isEmpty()) continue;
            var slotItem = slotStack.getItem();
            if (slotItem instanceof PocketJukeboxItem) {
                stack = slotStack;
            } else if (slotItem instanceof DyeItem dyeItem) {
                dye = dyeItem;
            }
        }

        var dyedItem = PocketJukeboxItem.get(dye == null ? null : dye.getDyeColor());
        return stack.transmuteCopy(dyedItem, 1);
    }

    public @Override boolean canCraftInDimensions(int width, int height) { return width * height >= 2; }
    public @Override RecipeSerializer<?> getSerializer() { return MuftRecipeSerializers.POCKET_JUKEBOX_COLORING.get(); }
}
