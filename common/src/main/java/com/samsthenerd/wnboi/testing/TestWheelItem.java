package com.samsthenerd.wnboi.testing;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TestWheelItem extends Item {
    public TestWheelItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(world.isClient()){
            MinecraftClient.getInstance().setScreen(new TestWheelScreen());
        }
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
    }
}
