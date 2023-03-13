package com.samsthenerd.wnboi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.samsthenerd.wnboi.testing.TestWheelItem;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class WNBOI implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("wnboi");

	public static final Item TEST_WHEEL_ITEM = new TestWheelItem(new Item.Settings().group(ItemGroup.TOOLS));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		Registry.register(Registry.ITEM, new Identifier("wnboi", "test_wheel_item"), TEST_WHEEL_ITEM);

		LOGGER.info("Hello Fabric world!");
	}
}
