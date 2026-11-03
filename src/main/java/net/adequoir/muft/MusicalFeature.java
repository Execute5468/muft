package net.adequoir.muft;

import net.adequoir.muft.registry.RegistryLoader;
import net.adequoir.muft.registry.misc.MuftItemEntries;
import net.adequoir.muft.registry.misc.MuftPacks;
import net.adequoir.muft.registry.misc.MuftPayloads;
import net.adequoir.muft.registry.tag.MuftItemTags;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicalFeature implements ModInitializer {
	public static final String MOD_ID = "muft";

	public static final Logger LOGGER = LoggerFactory.getLogger("MusicalFeature");

	public @Override void onInitialize() {
		MuftBootstrapper.init();
		RegistryLoader.init();
		MuftItemEntries.init();
		MuftItemTags.init();
		MuftPayloads.init();
		MuftPacks.init();
	}

	public static Identifier id(String path) { return Identifier.of(MOD_ID, path); }
}
