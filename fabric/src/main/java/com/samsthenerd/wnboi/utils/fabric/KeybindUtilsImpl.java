package com.samsthenerd.wnboi.utils.fabric;

import com.samsthenerd.wnboi.utils.KeybindUtils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

public class KeybindUtilsImpl {
    
    public static void registerKeybind(KeyBinding keyBinding, KeybindUtils.KeybindHandler handler){
        KeyBindingHelper.registerKeyBinding(keyBinding);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            handler.run(keyBinding, client);
        });
    }
}
