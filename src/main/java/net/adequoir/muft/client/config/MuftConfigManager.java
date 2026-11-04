package net.adequoir.muft.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.api.client.config.OptionsHelper;
import net.minecraft.client.OptionInstance;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public final class MuftConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static MuftConfig config = new MuftConfig();

    public static MuftConfig config() { return config; }

    static {
        if (!MuftConfig.FILE.exists()) {
            MusicalFeature.LOGGER.info("Config not detected, creating new config");
            save();
        } else {
            MusicalFeature.LOGGER.info("Loading existing config");
        }

        try (var reader = new FileReader(MuftConfig.FILE)) {
            config = GSON.fromJson(reader, MuftConfig.class);
        } catch (IOException e) {
            MusicalFeature.LOGGER.warn("Failed to load config", e);
        }
    }

    public static void save() {
        try (var writer = new FileWriter(MuftConfig.FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            MusicalFeature.LOGGER.warn("Failed to save config", e);
        }
    }

    public static OptionInstance<?>[] asOptions() {
        var options = new ArrayList<OptionInstance<?>>();
        for (var field: MuftConfig.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            var option = OptionsHelper.of(field);
            if (option != null) options.add(option);
        }
        return options.toArray(OptionInstance[]::new);
    }

    public static void init() {}
    private MuftConfigManager() {}
}
