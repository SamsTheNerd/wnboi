package com.samsthenerd.wnboi.utils;


import org.lwjgl.glfw.GLFW;

import com.samsthenerd.wnboi.interfaces.KeyboundItem;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class KeybindUtils {
    public static final KeyBinding DEFAULT_KEYBINDING = new KeyBinding("key.wnboi.default_context",
			InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.wnboi");

    public static final void handleKeyboundItems(KeyBinding keyBinding, MinecraftClient client){
        if(!keyBinding.isPressed()) return; // don't run if key isn't pressed
        PlayerEntity player = client.player;
        if(player == null){
            return;
        }
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();
        KeyboundItem mainItem = null;
        KeyboundItem offItem = null;

        if(mainHand.getItem() instanceof KeyboundItem){
            mainItem = (KeyboundItem)mainHand.getItem();
        }
        if(offHand.getItem() instanceof KeyboundItem){
            offItem = (KeyboundItem)offHand.getItem();
        }

        if(client.currentScreen != null){
            // there's some other screen open, probably shouldn't open ours
            return;
        } else {
            // no screen open - check if we should open one
            if(mainItem != null && mainItem.getKeyBinding() == keyBinding){
                mainItem.openScreen();
                return;
            }
            if(offItem != null && offItem.getKeyBinding() == keyBinding){
                offItem.openScreen();
                return;
            }
        }
    }

    public static void registerKeybinds(){
        registerKeybind(DEFAULT_KEYBINDING, (keyBinding, client) -> handleKeyboundItems(keyBinding, client));
    }

    @ExpectPlatform
    public static void registerKeybind(KeyBinding keyBinding, KeybindHandler handler){
        throw new AssertionError();
    }

    @FunctionalInterface
    public interface KeybindHandler{
        void run(KeyBinding keyBinding, MinecraftClient client);
    }
}
