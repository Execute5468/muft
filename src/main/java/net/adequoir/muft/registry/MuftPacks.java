package net.adequoir.muft.registry;

import net.adequoir.muft.resource.PackHelper;

public final class MuftPacks {

    static {
        PackHelper.registerPack("rebrushed_music_discs");
        PackHelper.initPacks();
    }

    public static void init() {}
    private MuftPacks() {}
}
