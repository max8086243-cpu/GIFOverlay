package com.gifoverlay.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.gifoverlay.config.ModConfig;
import com.gifoverlay.gui.GifSettingsScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class GifOverlayClient implements ClientModInitializer {
    private static KeyBinding openSettingsKey;
    private static boolean isDragging = false, isResizing = false;
    private static float dragStartX, dragStartY, resizeStartX, resizeStartY, startX, startY, startWidth, startHeight;
    
    @Override
    public void onInitializeClient() {
        // Исправленный конструктор для 1.21.11
        openSettingsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.gifoverlay.settings",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "category.gifoverlay"
        ));
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openSettingsKey.wasPressed()) {
                client.setScreen(new GifSettingsScreen(null));
            }
            boolean editMode = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), ModConfig.getInstance().editKey);
            if (editMode && client.mouse.wasLeftButtonClicked()) {
                startDragOrResize(client);
            }
            if (!editMode) {
                isDragging = false;
                isResizing = false;
            }
            if (isDragging && editMode && client.mouse.wasLeftButtonClicked()) {
                updateDrag(client);
            } else if (isResizing && editMode && client.mouse.wasLeftButtonClicked()) {
                updateResize(client);
            }
        });
        
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.getWindow() != null) {
                GifRenderer.getInstance().update();
                GifRenderer.getInstance().render(context, client.getWindow().getWidth(), client.getWindow().getHeight());
            }
        });
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && GifRenderer.getInstance().getTextureId() == null) {
                GifRenderer.getInstance().loadGif(ModConfig.getInstance().gifUrl);
            }
        });
    }
    
    private void startDragOrResize(MinecraftClient client) {
        ModConfig config = ModConfig.getInstance();
        double mx = client.mouse.getX() / client.getWindow().getScaleFactor();
        double my = client.mouse.getY() / client.getWindow().getScaleFactor();
        float sx = client.getWindow().getWidth();
        float sy = client.getWindow().getHeight();
        float gx = (config.xPos / 100f) * sx;
        float gy = (config.yPos / 100f) * sy;
        
        if (mx >= gx && mx <= gx + config.width && my >= gy && my <= gy + config.height) {
            if (client.mouse.wasRightButtonClicked()) {
                isResizing = true;
                resizeStartX = (float)mx;
                resizeStartY = (float)my;
                startWidth = config.width;
                startHeight = config.height;
            } else {
                isDragging = true;
                dragStartX = (float)mx;
                dragStartY = (float)my;
                startX = gx;
                startY = gy;
            }
        }
    }
    
    private void updateDrag(MinecraftClient client) {
        double mx = client.mouse.getX() / client.getWindow().getScaleFactor();
        double my = client.mouse.getY() / client.getWindow().getScaleFactor();
        ModConfig config = ModConfig.getInstance();
        float nx = startX + (float)mx - dragStartX;
        float ny = startY + (float)my - dragStartY;
        config.xPos = (nx / client.getWindow().getWidth()) * 100f;
        config.yPos = (ny / client.getWindow().getHeight()) * 100f;
        config.save();
    }
    
    private void updateResize(MinecraftClient client) {
        double mx = client.mouse.getX() / client.getWindow().getScaleFactor();
        double my = client.mouse.getY() / client.getWindow().getScaleFactor();
        ModConfig config = ModConfig.getInstance();
        config.width = Math.max(50, startWidth + (float)mx - resizeStartX);
        config.height = Math.max(50, startHeight + (float)my - resizeStartY);
        config.save();
    }
}
