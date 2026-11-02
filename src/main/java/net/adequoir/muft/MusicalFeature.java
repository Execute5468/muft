package net.adequoir.muft;

import net.adequoir.muft.registry.*;
import net.adequoir.muft.resource.PackHelper;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicalFeature implements ModInitializer {
	public static final String MOD_ID = "muft";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		MuftBlocks.init();
		MuftItems.init();
		MuftItemTags.init();
		MuftSoundEvents.init();
		MuftPayloads.init();
		MuftRecipeSerializers.init();
		MuftPacks.init();
	}

	public static Identifier id(String path) { return Identifier.of(MOD_ID, path); }
}
