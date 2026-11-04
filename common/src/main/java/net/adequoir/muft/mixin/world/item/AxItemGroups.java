package net.adequoir.muft.mixin.world.item;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeTabs.class)
public interface AxItemGroups {

    @Accessor("BUILDING_BLOCKS")
    static ResourceKey<CreativeModeTab> axBuildingBlocks() { throw new AssertionError(); }

    @Accessor("COLORED_BLOCKS")
    static ResourceKey<CreativeModeTab> axColoredBlocks() { throw new AssertionError(); }

    @Accessor("NATURAL_BLOCKS")
    static ResourceKey<CreativeModeTab> axNatural() { throw new AssertionError(); }

    @Accessor("FUNCTIONAL_BLOCKS")
    static ResourceKey<CreativeModeTab> axFunctional() { throw new AssertionError(); }

    @Accessor("REDSTONE_BLOCKS")
    static ResourceKey<CreativeModeTab> axRedstone() { throw new AssertionError(); }

    @Accessor("HOTBAR")
    static ResourceKey<CreativeModeTab> axHotbar() { throw new AssertionError(); }

    @Accessor("SEARCH")
    static ResourceKey<CreativeModeTab> axSearch() { throw new AssertionError(); }

    @Accessor("TOOLS_AND_UTILITIES")
    static ResourceKey<CreativeModeTab> axTools() { throw new AssertionError(); }

    @Accessor("COMBAT")
    static ResourceKey<CreativeModeTab> axCombat() { throw new AssertionError(); }

    @Accessor("FOOD_AND_DRINKS")
    static ResourceKey<CreativeModeTab> axFoodAndDrink() { throw new AssertionError(); }

    @Accessor("INGREDIENTS")
    static ResourceKey<CreativeModeTab> axIngredients() { throw new AssertionError(); }

    @Accessor("SPAWN_EGGS")
    static ResourceKey<CreativeModeTab> axSpawnEggs() { throw new AssertionError(); }

    @Accessor("OP_BLOCKS")
    static ResourceKey<CreativeModeTab> axOperator() { throw new AssertionError(); }

    @Accessor("INVENTORY")
    static ResourceKey<CreativeModeTab> axInventory() { throw new AssertionError(); }
}
