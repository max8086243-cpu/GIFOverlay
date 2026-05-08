package com.gifoverlay.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import com.gifoverlay.config.ModConfig;
import com.gifoverlay.gui.GifSettingsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class GifOverlayClient implements ClientModInitializer {
    private static KeyMapping openSettingsKey;
    private static boolean isDragging = false, isResizing = false;
    private static float dragStartX, dragStartY, resizeStartX, resizeStartY, startX, startY, startWidth, startHeight;

    @Override
    public void onInitializeClient() {
        // Исправлено для 1.21.11: используем новую категорию KeyMapping
        openSettingsKey = KeyBindingHelper.registerKeyMapping(new KeyMapping(
            "key.gifoverlay.settings",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.gifoverlay"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openSettingsKey.consumeClick()) {
                if (client.player != null) {
                    client.setScreen(new GifSettingsScreen(null));
                }
            }
            boolean editMode = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), ModConfig.getInstance().editKey);
            if (editMode && client.mouseHandler.isLeftPressed()) {
                startDragOrResize(client);
            }
            if (!editMode) {
                isDragging = false;
                isResizing = false;
            }
            if (isDragging && editMode && client.mouseHandler.isLeftPressed()) {
                updateDrag(client);
            } else if (isResizing && editMode && client.mouseHandler.isLeftPressed()) {
                updateResize(client);
            }
        });

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            Minecraft client = Minecraft.getInstance();
            if (client.player != null && client.getWindow() != null) {
                GifRenderer.getInstance().update();
                GifRenderer.getInstance().render(context, client.getWindow().getGuiScaledWidth(), client.getWindow().getGuiScaledHeight());
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && GifRenderer.getInstance().getTextureId() == null) {
                GifRenderer.getInstance().loadGif(ModConfig.getInstance().gifUrl);
            }
        });
    }

    private void startDragOrResize(Minecraft client) {
        ModConfig config = ModConfig.getInstance();
        double mx = client.mouseHandler.xpos() * (double) client.getWindow().getGuiScaledWidth() / client.getWindow().getScreenWidth();
        double my = client.mouseHandler.ypos() * (double) client.getWindow().getGuiScaledHeight() / client.getWindow().getScreenHeight();
        float sx = client.getWindow().getGuiScaledWidth();
        float sy = client.getWindow().getGuiScaledHeight();
        float gx = (config.xPos / 100f) * sx;
        float gy = (config.yPos / 100f) * sy;
        
        if (mx >= gx && mx <= gx + config.width && my >= gy && my <= gy + config.height) {
            if (client.mouseHandler.isRightPressed()) {
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
    
    private void updateDrag(Minecraft client) {
        double mx = client.mouseHandler.xpos() * (double) client.getWindow().getGuiScaledWidth() / client.getWindow().getScreenWidth();
        double my = client.mouseHandler.ypos() * (double) client.getWindow().getGuiScaledHeight() / client.getWindow().getScreenHeight();
        ModConfig config = ModConfig.getInstance();
        float nx = startX + (float)mx - dragStartX;
        float ny = startY + (float)my - dragStartY;
        config.xPos = (nx / client.getWindow().getGuiScaledWidth()) * 100f;
        config.yPos = (ny / client.getWindow().getGuiScaledHeight()) * 100f;
        config.save();
    }
    
    private void updateResize(Minecraft client) {
        double mx = client.mouseHandler.xpos() * (double) client.getWindow().getGuiScaledWidth() / client.getWindow().getScreenWidth();
        double my = client.mouseHandler.ypos() * (double) client.getWindow().getGuiScaledHeight() / client.getWindow().getScreenHeight();
        ModConfig config = ModConfig.getInstance();
        config.width = Math.max(50, startWidth + (float)mx - resizeStartX);
        config.height = Math.max(50, startHeight + (float)my - resizeStartY);
        config.save();
    }
}
