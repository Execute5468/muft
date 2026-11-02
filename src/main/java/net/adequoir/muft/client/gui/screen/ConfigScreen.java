package net.adequoir.muft.client.gui.screen;

import net.adequoir.muft.client.config.MuftConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.text.Text;

public final class ConfigScreen extends GameOptionsScreen {
    private static final Text TITLE = Text.translatable("muft.config.title");

    public ConfigScreen(Screen parent) { super(parent, MinecraftClient.getInstance().options, TITLE); }

    protected @Override void addOptions() {
        if (body == null) return;
        body.addAll(MuftConfigManager.asOptions());
    }

    public @Override void removed() { MuftConfigManager.save(); }
}
