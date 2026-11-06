package net.adequoir.muft;

import net.adequoir.muft.client.config.MuftConfigManager;
import net.adequoir.muft.client.registry.MuftScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = Muft.MOD_ID, value = Dist.CLIENT)
public class MusicalFeatureClient {
    @SubscribeEvent
    public static void initialize(FMLClientSetupEvent event) {
        MuftConfigManager.init();
        MuftScreens.init();
    }
}
