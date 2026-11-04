package net.adequoir.muft.resource;

import com.mojang.logging.LogUtils;
import net.adequoir.muft.api.resource.PackHelper;
import net.adequoir.muft.api.resource.PackState;
import net.adequoir.muft.api.resource.PackStorage;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;

public class FabricPackHelper extends PackHelper {
    private static final Logger LOGGER = LogUtils.getLogger();

    protected @Override void register(PackStorage storage) {
        FabricLoader.getInstance().getModContainer(storage.namespace()).ifPresent(container -> {
            var packEntries = storage.entries();
            for (int i = 0; i < packEntries.size(); i++) {
                var entry = packEntries.get(i);
                try {
                    ResourceManagerHelper.registerBuiltinResourcePack(
                        entry.id(),
                        container,
                        entry.translationKey(),
                        toFabricType(entry.state())
                    );
                    LOGGER.info("Registered built-in {} '{}'", getPackName(entry.type()), entry.id());
                } catch (Exception e) {
                    LOGGER.error("Failed to register built-in {} '{}'", getPackName(entry.type()), entry.id());
                    LOGGER.error(e.getLocalizedMessage());
                }
            }
        });
    }

    private String getPackName(PackType type) { return type == PackType.CLIENT_RESOURCES ? "Resource Pack" : "Data Pack"; }

    private static ResourcePackActivationType toFabricType(PackState state) {
        return switch (state) {
            case DISABLED -> ResourcePackActivationType.NORMAL;
            case ENABLED -> ResourcePackActivationType.DEFAULT_ENABLED;
            case FORCED -> ResourcePackActivationType.ALWAYS_ENABLED;
        };
    }
}
