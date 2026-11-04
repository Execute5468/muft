package net.adequoir.muft.registry.misc;

import net.adequoir.muft.Muft;
import net.adequoir.muft.api.resource.PackHelper;
import net.adequoir.muft.api.resource.PackState;
import net.adequoir.muft.util.GameState;

public final class MuftPacks {

    static {
        PackHelper.build(Muft.MOD_ID, storage -> {
            storage.registerResource("rebrushed_music_discs");
            storage.registerData("diamond_pocket_jukeboxes");
            if (!GameState.hasBackport()) return;
            Muft.LOGGER.info("Vanilla Backport detected!");
            storage.registerData("backport_recipe_compat", PackState.ENABLED);
            storage.registerData("resin_blank_disc");
        });
    }

    public static void init() {}
    private MuftPacks() {}
}
