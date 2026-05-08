package com.gifoverlay.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.gifoverlay.gui.GifSettingsScreen;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
@Override
public ConfigScreenFactory<?> getModConfigScreenFactory() {
return parent -> new GifSettingsScreen(parent);
}
}