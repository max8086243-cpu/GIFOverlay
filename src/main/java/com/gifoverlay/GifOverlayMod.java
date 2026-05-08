package com.gifoverlay;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GifOverlayMod implements ModInitializer {
public static final String MOD_ID = "gifoverlay";
public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

@Override
public void onInitialize() {
LOGGER.info("[GIF Overlay] Мод загружен!");
}
}
