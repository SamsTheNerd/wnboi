package com.samsthenerd.wnboi.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.samsthenerd.wnboi.utils.GetTextureManagerInterface;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.SynchronousResourceReloader;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRendererTextureManager implements SynchronousResourceReloader, GetTextureManagerInterface{
    @Shadow
    private @Final TextureManager textureManager;

    @Override
    public TextureManager getTextureManager(){
        return textureManager;
    }
}
