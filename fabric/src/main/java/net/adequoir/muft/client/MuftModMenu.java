package net.adequoir.muft.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.adequoir.muft.client.gui.screen.ConfigScreen;

public class MuftModMenu implements ModMenuApi {
    public @Override ConfigScreenFactory<?> getModConfigScreenFactory() { return ConfigScreen::new; }
}