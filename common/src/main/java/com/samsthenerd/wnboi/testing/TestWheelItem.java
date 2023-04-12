package com.samsthenerd.wnboi.testing;

import com.samsthenerd.wnboi.interfaces.KeyboundItem;
import com.samsthenerd.wnboi.screen.AbstractContextWheelScreen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TestWheelItem extends Item implements KeyboundItem{

    TestWheelScreen screen = null;
    public TestWheelItem(Settings settings) {
        super(settings);
        screen = new TestWheelScreen();
        screen.keyBinding = this.getKeyBinding();
        screen.requireKeydown = true;
    }

    public AbstractContextWheelScreen getScreen(){
        return screen;
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(world.isClient()){
            MinecraftClient.getInstance().setScreen(getScreen());
        }
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
    }
}
