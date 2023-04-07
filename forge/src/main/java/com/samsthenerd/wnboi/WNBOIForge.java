package com.samsthenerd.wnboi;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(WNBOI.MOD_ID)
public class WNBOIForge {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WNBOI.MOD_ID);

    public static final RegistryObject<Item> TEST_WHEEL_ITEM = ITEMS.register("test_wheel_item", () -> WNBOI.TEST_WHEEL_ITEM);

    public WNBOIForge() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
