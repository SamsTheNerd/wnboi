package com.samsthenerd.wnboi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.samsthenerd.wnboi.testing.TestWheelItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class WNBOI {
	public static final String MOD_ID = "wnboi";
	public static final Item TEST_WHEEL_ITEM = new TestWheelItem(new Item.Settings().group(ItemGroup.TOOLS));

	public static final Logger LOGGER = LoggerFactory.getLogger("wnboi");
}
