package net.adequoir.muft.client.registry;

import net.adequoir.muft.client.gui.screen.DiscPressScreen;

import static net.adequoir.muft.registry.MuftScreenHandlers.DISC_PRESS;
import static net.minecraft.client.gui.screen.ingame.HandledScreens.register;

public final class MuftScreens {

    static {
        register(DISC_PRESS, DiscPressScreen::new);
    }

    public static void init() {}
    private MuftScreens() {}
}
