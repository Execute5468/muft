package net.adequoir.muft;

import net.adequoir.muft.client.config.MuftConfigManager;
import net.adequoir.muft.client.gui.screen.ConfigScreen;
import net.adequoir.muft.client.registry.MuftScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Muft.MOD_ID, dist = Dist.CLIENT)
public class MusicalFeatureClient {
    public MusicalFeatureClient(IEventBus bus) {
        bus.addListener(MusicalFeatureClient::onClientInit);
    }

    static void onClientInit(FMLClientSetupEvent modBus) {
        MuftConfigManager.init();
        MuftScreens.init();
        MusicalFeature.container.registerExtensionPoint(IConfigScreenFactory.class, (minecraft, parent) -> new ConfigScreen(parent));
    }
}
