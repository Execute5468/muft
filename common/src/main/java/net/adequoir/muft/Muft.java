package net.adequoir.muft;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Muft {
	public static final String MOD_ID = "muft";

	public static final Logger LOGGER = LoggerFactory.getLogger("MusicalFeature");



	public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(MOD_ID, path); }
}
