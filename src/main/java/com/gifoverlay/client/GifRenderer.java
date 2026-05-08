package com.gifoverlay.client;

import com.gifoverlay.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class GifRenderer {
private static GifRenderer instance;
private NativeImageBackedTexture texture;
private Identifier textureId;
private int gifWidth, gifHeight;
private long lastFrameTime = 0;
private int currentFrame = 0;
private byte[] gifData;

public static GifRenderer getInstance() {
if (instance == null) instance = new GifRenderer();
return instance;
}

public void loadGif(String urlString) {
CompletableFuture.runAsync(() -> {
try {
HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
conn.setRequestProperty("User-Agent", "Mozilla/5.0");
ByteArrayOutputStream baos = new ByteArrayOutputStream();
try (InputStream is = conn.getInputStream()) {
byte[] buffer = new byte[4096];
int read;
while ((read = is.read(buffer)) != -1) {
baos.write(buffer, 0, read);
}
}
gifData = baos.toByteArray();

MinecraftClient.getInstance().execute(() -> {
try {
BufferedImage img = ImageIO.read(new ByteArrayInputStream(gifData));
if (img != null) {
if (texture != null) texture.close();
NativeImage nativeImage = bufferedImageToNativeImage(img);
texture = new NativeImageBackedTexture(nativeImage);
textureId = Identifier.of("gifoverlay", "gif_texture");
MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, texture);
gifWidth = img.getWidth();
gifHeight = img.getHeight();
}
} catch (Exception e) {
e.printStackTrace();
}
});
} catch (Exception e) {
e.printStackTrace();
}
});
}

private BufferedImage getFrame(int frame) {
try {
return ImageIO.read(new ByteArrayInputStream(gifData));
} catch (Exception e) {
return null;
}
}

private NativeImage bufferedImageToNativeImage(BufferedImage img) {
NativeImage nativeImage = new NativeImage(img.getWidth(), img.getHeight(), false);
for (int x = 0; x < img.getWidth(); x++) {
for (int y = 0; y < img.getHeight(); y++) {
nativeImage.setColor(x, y, img.getRGB(x, y));
}
}
return nativeImage;
}

public void render(DrawContext context, int screenWidth, int screenHeight) {
if (textureId == null || gifWidth <= 0 || gifHeight <= 0) return;

ModConfig config = ModConfig.getInstance();
float x = (config.xPos / 100f) * screenWidth;
float y = (config.yPos / 100f) * screenHeight;
float w = config.width;
float h = config.height;

context.drawTexture(textureId, (int)x, (int)y, (int)w, (int)h, 0, 0, gifWidth, gifHeight, gifWidth, gifHeight);
}

public void update() {
if (gifData == null) return;
if (System.currentTimeMillis() - lastFrameTime > 100) {
currentFrame++;
lastFrameTime = System.currentTimeMillis();
BufferedImage img = getFrame(currentFrame);
if (img != null) {
NativeImage nativeImage = bufferedImageToNativeImage(img);
if (texture != null) texture.close();
texture = new NativeImageBackedTexture(nativeImage);
MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, texture);
gifWidth = img.getWidth();
gifHeight = img.getHeight();
} else if (ModConfig.getInstance().loop) {
currentFrame = 0;
}
}
}

public Identifier getTextureId() { return textureId; }
}