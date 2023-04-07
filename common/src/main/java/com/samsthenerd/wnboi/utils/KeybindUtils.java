package com.samsthenerd.wnboi.utils;

import org.lwjgl.glfw.GLFW;

import com.samsthenerd.wnboi.WNBOI;
import com.samsthenerd.wnboi.interfaces.KeyboundItem;
import com.samsthenerd.wnboi.screen.AbstractContextWheelScreen;

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

        // first check if the current open screen is either of these
        if(client.currentScreen instanceof AbstractContextWheelScreen){ 
            // some context wheel is open - check if we should close it
            if(mainItem != null && mainItem.screenIsOpen() && mainItem.getKeyBinding() == keyBinding){
                if(!keyBinding.wasPressed()){
                    WNBOI.LOGGER.info("closing main item screen with keybind: " + keyBinding.getTranslationKey());
                    mainItem.closeScreen();
                    return;
                }
            }
            if(offItem != null && offItem.screenIsOpen() && offItem.getKeyBinding() == keyBinding){
                if(!keyBinding.wasPressed()){
                    WNBOI.LOGGER.info("closing off item screen with keybind: " + keyBinding.getTranslationKey());
                    offItem.closeScreen();
                    return;
                }
            }
            // neither item is a keybound item, if we have a wheel context screen open we should remind it to close if it needs to
            if(offItem == null && mainItem == null){
                ((AbstractContextWheelScreen)client.currentScreen).askToClose(0);
                return;
            }

        } else if(client.currentScreen != null){
            // there's some other screen open, probably shouldn't open ours
            return;
        } else {
            // no screen open - check if we should open one
            if(mainItem != null && mainItem.getKeyBinding() == keyBinding && keyBinding.isPressed()){
                mainItem.openScreen();
                return;
            }
            if(offItem != null && offItem.getKeyBinding() == keyBinding && keyBinding.isPressed()){
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
