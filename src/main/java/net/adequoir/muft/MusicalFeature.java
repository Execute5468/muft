package net.adequoir.muft;

import net.adequoir.muft.registry.RegistryLoader;
import net.adequoir.muft.registry.api.FabricRegistrar;
import net.adequoir.muft.registry.api.Registrars;
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

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public @Override void onInitialize() {
		Registrars.bootstrap(FabricRegistrar.FACTORY);
		RegistryLoader.init();
		MuftItemEntries.init();
		MuftItemTags.init();
		MuftPayloads.init();
		MuftPacks.init();
	}

	public static Identifier id(String path) { return Identifier.of(MOD_ID, path); }
}
