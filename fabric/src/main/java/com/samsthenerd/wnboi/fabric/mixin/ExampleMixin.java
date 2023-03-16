package com.samsthenerd.wnboi.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.screen.TitleScreen;

@Mixin(TitleScreen.class)
public class ExampleMixin {
	// @Inject(at = @At("HEAD"), method = "init()V")
	// private void init(CallbackInfo info) {
	// 	WNBOI.LOGGER.info("This line is printed by an example mod mixin!");
	// }
}
