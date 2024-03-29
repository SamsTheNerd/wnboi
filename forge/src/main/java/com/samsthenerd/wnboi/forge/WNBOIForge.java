package com.samsthenerd.wnboi.forge;

import com.samsthenerd.wnboi.WNBOI;

import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(WNBOI.MOD_ID)
public class WNBOIForge {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WNBOI.MOD_ID);

    // public static final RegistryObject<Item> TEST_WHEEL_ITEM = ITEMS.register("test_wheel_item", () -> WNBOI.TEST_WHEEL_ITEM);

    public WNBOIForge() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        if (FMLEnvironment.dist == Dist.CLIENT){
            WNBOIForgeClient.doClientStuff();
        }
    }
}
