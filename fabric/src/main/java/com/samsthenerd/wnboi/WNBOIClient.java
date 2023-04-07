package com.samsthenerd.wnboi;

import com.samsthenerd.wnboi.utils.KeybindUtils;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WNBOIClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeybindUtils.registerKeybinds();
    }
}
