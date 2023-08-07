package com.samsthenerd.wnboi.forge;

import com.samsthenerd.wnboi.utils.KeybindUtils;

public class WNBOIForgeClient {
    public static void doClientStuff(){
        KeybindUtils.registerKeybinds();
        // MinecraftForge.EVENT_BUS.register(WNBOIForgeClient.class);
        // FMLJavaModLoadingContext.get().getModEventBus().register(WNBOIForgeClient.class);
    }
}
