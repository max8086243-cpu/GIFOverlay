package com.gifoverlay.gui;

import com.gifoverlay.client.GifRenderer;
import com.gifoverlay.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class GifSettingsScreen extends Screen {
    private TextFieldWidget urlField;
    private TextFieldWidget xField;
    private TextFieldWidget yField;
    private TextFieldWidget widthField;
    private TextFieldWidget heightField;
    private TextFieldWidget keyField;
    private ButtonWidget loopButton;
    private ModConfig config;
    
    public GifSettingsScreen(Screen parent) {
        super(Text.literal("GIF Overlay Settings"));
        this.config = ModConfig.getInstance();
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 40;
        
        // URL
        this.addDrawableChild(ButtonWidget.builder(Text.literal("GIF URL:"), button -> {})
            .dimensions(centerX - 150, y, 100, 20).build());
        
        urlField = new TextFieldWidget(this.textRenderer, centerX - 40, y, 190, 20, Text.literal("URL"));
        urlField.setText(config.gifUrl);
        this.addSelectableChild(urlField);
        y += 30;
        
        // X позиция
        this.addDrawableChild(ButtonWidget.builder(Text.literal("X (%):"), button -> {})
            .dimensions(centerX - 150, y, 100, 20).build());
        
        xField = new TextFieldWidget(this.textRenderer, centerX - 40, y, 80, 20, Text.literal("X"));
        xField.setText(String.valueOf(config.xPos));
        this.addSelectableChild(xField);
        
        // Y позиция
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Y (%):"), button -> {})
            .dimensions(centerX + 60, y, 100, 20).build());
        
        yField = new TextFieldWidget(this.textRenderer, centerX + 170, y, 80, 20, Text.literal("Y"));
        yField.setText(String.valueOf(config.yPos));
        this.addSelectableChild(yField);
        y += 30;
        
        // Ширина
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Width:"), button -> {})
            .dimensions(centerX - 150, y, 100, 20).build());
        
        widthField = new TextFieldWidget(this.textRenderer, centerX - 40, y, 80, 20, Text.literal("px"));
        widthField.setText(String.valueOf(config.width));
        this.addSelectableChild(widthField);
        
        // Высота
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Height:"), button -> {})
            .dimensions(centerX + 60, y, 100, 20).build());
        
        heightField = new TextFieldWidget(this.textRenderer, centerX + 170, y, 80, 20, Text.literal("px"));
        heightField.setText(String.valueOf(config.height));
        this.addSelectableChild(heightField);
        y += 30;
        
        // Клавиша редактирования
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Edit Key (LWJGL):"), button -> {})
            .dimensions(centerX - 150, y, 120, 20).build());
        
        keyField = new TextFieldWidget(this.textRenderer, centerX - 20, y, 60, 20, Text.literal("key"));
        keyField.setText(String.valueOf(config.editKey));
        this.addSelectableChild(keyField);
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("? (R=19)"), button -> {})
            .dimensions(centerX + 50, y, 80, 20).build());
        y += 30;
        
        // Кнопка зацикливания
        loopButton = ButtonWidget.builder(
            Text.literal("Loop: " + (config.loop ? "ON" : "OFF")),
            button -> {
                config.loop = !config.loop;
                loopButton.setMessage(Text.literal("Loop: " + (config.loop ? "ON" : "OFF")));
            }
        ).dimensions(centerX - 150, y, 100, 20).build();
        this.addDrawableChild(loopButton);
        y += 40;
        
        // Кнопка сохранить
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save & Reload"), button -> {
            saveSettings();
            GifRenderer.getInstance().loadGif(config.gifUrl);
            this.close();
        }).dimensions(centerX - 100, y, 80, 20).build());
        
        // Кнопка отмена
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> {
            this.close();
        }).dimensions(centerX + 20, y, 80, 20).build());
    }
    
    private void saveSettings() {
        try {
            config.gifUrl = urlField.getText();
            config.xPos = Float.parseFloat(xField.getText());
            config.yPos = Float.parseFloat(yField.getText());
            config.width = Float.parseFloat(widthField.getText());
            config.height = Float.parseFloat(heightField.getText());
            config.editKey = Integer.parseInt(keyField.getText());
            config.save();
        } catch (NumberFormatException ignored) {}
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        urlField.render(context, mouseX, mouseY, delta);
        xField.render(context, mouseX, mouseY, delta);
        yField.render(context, mouseX, mouseY, delta);
        widthField.render(context, mouseX, mouseY, delta);
        heightField.render(context, mouseX, mouseY, delta);
        keyField.render(context, mouseX, mouseY, delta);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (urlField.keyPressed(keyCode, scanCode, modifiers) ||
            xField.keyPressed(keyCode, scanCode, modifiers) ||
            yField.keyPressed(keyCode, scanCode, modifiers) ||
            widthField.keyPressed(keyCode, scanCode, modifiers) ||
            heightField.keyPressed(keyCode, scanCode, modifiers) ||
            keyField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
