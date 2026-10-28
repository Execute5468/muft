package net.adequoir.muft;

import net.adequoir.muft.registry.MuftItemTags;
import net.adequoir.muft.registry.MuftItems;
import net.adequoir.muft.registry.MuftPayloads;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicalFeature implements ModInitializer {
	public static final String MOD_ID = "muft";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		MuftItems.init();
		MuftItemTags.init();
		MuftPayloads.init();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
