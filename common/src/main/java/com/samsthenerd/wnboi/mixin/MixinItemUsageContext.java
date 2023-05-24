package com.samsthenerd.wnboi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.hit.BlockHitResult;

@Mixin(ItemUsageContext.class)
public interface MixinItemUsageContext {
    @Invoker("getHitResult")
    public BlockHitResult invokeGetHitResult();
}
