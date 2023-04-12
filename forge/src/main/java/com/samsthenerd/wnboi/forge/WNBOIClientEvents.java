package com.samsthenerd.wnboi.forge;

import com.samsthenerd.wnboi.WNBOI;
import com.samsthenerd.wnboi.utils.KeybindUtils;
import com.samsthenerd.wnboi.utils.forge.KeybindUtilsImpl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Pair;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WNBOI.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WNBOIClientEvents {
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) { // Only call code once as the tick event is called twice every tick
            for(Pair<KeyBinding, KeybindUtils.KeybindHandler> keybindingsPair : KeybindUtilsImpl.keybindingsToRegister){
                keybindingsPair.getRight().run(keybindingsPair.getLeft(), MinecraftClient.getInstance());
            }
        }
    }

    @Mod.EventBusSubscriber(modid = WNBOI.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents{   
        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            for(Pair<KeyBinding, KeybindUtils.KeybindHandler> keybindingsPair : KeybindUtilsImpl.keybindingsToRegister){
                event.register(keybindingsPair.getLeft());
            }
        }
    }
}
