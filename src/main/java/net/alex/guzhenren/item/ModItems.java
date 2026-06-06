package net.alex.guzhenren.item;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.item.gu.*;
import net.alex.guzhenren.item.material.*;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Guzhenren.MOD_ID);

    public static final DeferredItem<Item> HOPE_GU =
            ITEMS.register("hope_gu",
                    () -> new HopeGu(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> ESSENCE_STONE =
            ITEMS.register("essence_stone",
                    () -> new EssenceStone(new Item.Properties()));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}