package com.samsthenerd.wnboi;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.Screens;

@Environment(EnvType.CLIENT)
public class WNBOIClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Screens.register();
    }
}
