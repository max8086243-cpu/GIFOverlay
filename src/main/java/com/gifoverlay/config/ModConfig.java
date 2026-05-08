package com.gifoverlay.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class ModConfig {
private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("gifoverlay.json");
private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

public String gifUrl = "https://media.tenor.com/5kA3j7ZgPsEAAAAC/minecraft-creeper.gif";
public boolean loop = true;
public float xPos = 10f;
public float yPos = 10f;
public float width = 200f;
public float height = 150f;
public int editKey = 19;

private static ModConfig instance;

public static ModConfig getInstance() {
if (instance == null) {
instance = load();
}
return instance;
}

private static ModConfig load() {
if (CONFIG_PATH.toFile().exists()) {
try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
return GSON.fromJson(reader, ModConfig.class);
} catch (IOException e) {
e.printStackTrace();
}
}
return new ModConfig();
}

public void save() {
try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
GSON.toJson(this, writer);
} catch (IOException e) {
e.printStackTrace();
}
}
}