package com.samsthenerd.wnboi.interfaces;

import com.samsthenerd.wnboi.screen.AbstractContextWheelScreen;
import com.samsthenerd.wnboi.utils.KeybindUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

// 
public interface KeyboundItem{
    // override this if you want to use a different keybinding
    // see KeybindUtils.DEFAULT_KEYBINDING for an example of how to make a new one
    @Environment(EnvType.CLIENT)
    public default KeyBinding getKeyBinding(){
        return KeybindUtils.DEFAULT_KEYBINDING;
    }

    // needs to return the relevant instance of the screen. so make a new one or grab the current one if it's already open
    // can check with (MinecraftClient.getInstance().currentScreen instanceof YourContextWheelScreen) 
    @Environment(EnvType.CLIENT)
    public AbstractContextWheelScreen getScreen();

    // checks if the screen is open. the default impl may not work depending on how you implement getScreen()
    @Environment(EnvType.CLIENT)
    public default boolean screenIsOpen(){
        return MinecraftClient.getInstance().currentScreen == getScreen();
    }

    @Environment(EnvType.CLIENT)
    public default void openScreen(){
        MinecraftClient.getInstance().setScreen(getScreen());
    }
}
