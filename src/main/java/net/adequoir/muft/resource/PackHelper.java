package net.adequoir.muft.resource;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.util.FabricDependant;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PackHelper {
    private static final ArrayList<Entry> packEntries = new ArrayList<>();

    public static void registerPack(String path) { registerPack(path, PackActivationType.NORMAL); }

    public static void registerPack(String path, PackActivationType activationType) {
        packEntries.add(new Entry(MusicalFeature.id(path), activationType));
    }

    public static @UnmodifiableView List<Entry> packEntries() { return Collections.unmodifiableList(packEntries); }

    public static void initPacks() {
        registerFabricPacks();
    }

    @FabricDependant
    private static void registerFabricPacks() {
        FabricLoader.getInstance().getModContainer(MusicalFeature.MOD_ID).ifPresent(container -> {
            MusicalFeature.LOGGER.info("Registering built-in packs");

            var packEntries = packEntries();
            for (int i = 0; i < packEntries.size(); i++) {
                var entry = packEntries.get(i);
                try {
                    ResourceManagerHelper.registerBuiltinResourcePack(
                        entry.id(),
                        container,
                        Text.translatable("resourcePack." + entry.id().toTranslationKey() + ".name"),
                        toFabricType(entry.activationType())
                    );
                    MusicalFeature.LOGGER.info("Registered built-in pack {}", entry.id());
                } catch (Exception e) {
                    MusicalFeature.LOGGER.error("Failed to register built-in pack {}", entry.id());
                    MusicalFeature.LOGGER.error(e.getLocalizedMessage());
                }
            }
        });
    }

    @FabricDependant
    private static ResourcePackActivationType toFabricType(PackActivationType activationType) {
        return switch (activationType) {
            case NORMAL -> ResourcePackActivationType.NORMAL;
            case DEFAULT_ENABLED -> ResourcePackActivationType.DEFAULT_ENABLED;
            case ALWAYS_ENABLED -> ResourcePackActivationType.ALWAYS_ENABLED;
        };
    }

    public record Entry(Identifier id, PackActivationType activationType) {}
}
