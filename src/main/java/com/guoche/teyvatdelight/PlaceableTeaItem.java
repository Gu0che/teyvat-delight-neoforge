package com.guoche.teyvatdelight;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/** @deprecated Use {@link com.guoche.teyvatdelight.food.PlaceableTeaItem} for new integrations. */
@Deprecated
public class PlaceableTeaItem extends com.guoche.teyvatdelight.food.PlaceableTeaItem {
    public PlaceableTeaItem(Block block, Item.Properties properties) {
        super(block, properties);
    }
}
