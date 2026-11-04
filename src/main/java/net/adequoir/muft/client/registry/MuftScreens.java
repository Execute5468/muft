package net.adequoir.muft.client.registry;

import net.adequoir.muft.client.gui.screen.DiscPressScreen;

import static net.adequoir.muft.registry.MuftSHandlers.DISC_PRESS;
import static net.minecraft.client.gui.screens.MenuScreens.register;

public final class MuftScreens {

    static {
        register(DISC_PRESS.get(), DiscPressScreen::new);
    }

    public static void init() {}
    private MuftScreens() {}
}
