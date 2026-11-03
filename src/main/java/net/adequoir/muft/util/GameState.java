package net.adequoir.muft.util;

import net.fabricmc.loader.api.FabricLoader;

public class GameState {
    private static final String VANILLA_BACKPORT = "vanillabackport";

    @FabricDependant
    private static final boolean HAS_BACKPORT = FabricLoader.getInstance().isModLoaded(VANILLA_BACKPORT);

    public static boolean hasBackport() { return HAS_BACKPORT; }
}
