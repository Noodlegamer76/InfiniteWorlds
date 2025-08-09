package com.noodlegamer76.infiniteworlds.item;

import com.noodlegamer76.infiniteworlds.InfiniteWorlds;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class InitItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.Items.createItems(InfiniteWorlds.MODID);

    public static final DeferredHolder<Item, TestItem> TEST_ITEM = ITEMS.register("test_item" ,
    () -> new TestItem(new Item.Properties()));

    //public static final DeferredHolder<Item, BlockItem> PORTAL = ITEMS.register("portal" ,
    //        () -> new BlockItem(InitBlocks.PORTAL.get(), new Item.Properties()));
}
