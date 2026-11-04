package net.adequoir.muft.util;

import net.adequoir.muft.api.LoaderUtil;

public class GameState {
    private static final String VANILLA_BACKPORT = "vanillabackport";

    private static final boolean HAS_BACKPORT = LoaderUtil.isLoaded(VANILLA_BACKPORT);

    public static boolean hasBackport() { return HAS_BACKPORT; }
}
