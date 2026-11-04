package net.adequoir.muft.api.item;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import static net.adequoir.muft.mixin.world.item.AxItemGroups.*;

public interface ItemGroups {
    ResourceKey<CreativeModeTab> BUILDING_BLOCKS = axBuildingBlocks();
    ResourceKey<CreativeModeTab> COLORED_BLOCKS = axColoredBlocks();
    ResourceKey<CreativeModeTab> NATURAL = axNatural();
    ResourceKey<CreativeModeTab> FUNCTIONAL = axFunctional();
    ResourceKey<CreativeModeTab> REDSTONE = axRedstone();
    ResourceKey<CreativeModeTab> HOTBAR = axHotbar();
    ResourceKey<CreativeModeTab> SEARCH = axSearch();
    ResourceKey<CreativeModeTab> TOOLS = axTools();
    ResourceKey<CreativeModeTab> COMBAT = axCombat();
    ResourceKey<CreativeModeTab> FOOD_AND_DRINK = axFoodAndDrink();
    ResourceKey<CreativeModeTab> INGREDIENTS = axIngredients();
    ResourceKey<CreativeModeTab> SPAWN_EGGS = axSpawnEggs();
    ResourceKey<CreativeModeTab> OPERATOR = axOperator();
    ResourceKey<CreativeModeTab> INVENTORY = axInventory();
}
