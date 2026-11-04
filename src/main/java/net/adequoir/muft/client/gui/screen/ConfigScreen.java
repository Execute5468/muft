package net.adequoir.muft.client.gui.screen;

import net.adequoir.muft.client.config.MuftConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public final class ConfigScreen extends OptionsSubScreen {
    private static final Component TITLE = Component.translatable("muft.config.title");

    public ConfigScreen(Screen parent) { super(parent, Minecraft.getInstance().options, TITLE); }

    protected @Override void addOptions() {
        if (list == null) return;
        list.addSmall(MuftConfigManager.asOptions());
    }

    public @Override void removed() { MuftConfigManager.save(); }
}
