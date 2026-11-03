package net.adequoir.muft.registry;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.resource.PackActivationType;
import net.adequoir.muft.resource.PackHelper;
import net.adequoir.muft.util.GameState;

public final class MuftPacks {

    static {
        PackHelper.registerPack("rebrushed_music_discs");
        PackHelper.registerPack("diamond_pocket_jukeboxes");
        if (GameState.hasBackport()) {
            MusicalFeature.LOGGER.info("Vanilla Backport detected!");
            PackHelper.registerPack("backport_recipe_compat", PackActivationType.DEFAULT_ENABLED);
            PackHelper.registerPack("resin_blank_disc");
        }
        PackHelper.initPacks();
    }

    public static void init() {}
    private MuftPacks() {}
}
