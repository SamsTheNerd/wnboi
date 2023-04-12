package com.samsthenerd.wnboi.utils.forge;

import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.wnboi.utils.KeybindUtils;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Pair;

public class KeybindUtilsImpl {

    public static List<Pair<KeyBinding, KeybindUtils.KeybindHandler> > keybindingsToRegister = new ArrayList<>();

    public static void registerKeybind(KeyBinding keyBinding, KeybindUtils.KeybindHandler handler){
        keybindingsToRegister.add(new Pair<>(keyBinding, handler));
    }
}
