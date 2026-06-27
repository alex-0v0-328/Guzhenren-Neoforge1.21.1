package net.alex.guzhenren.registry;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.item.EssenceStoneItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Guzhenren.MOD_ID);

    public static final DeferredItem<EssenceStoneItem> ESSENCE_STONE =
            ITEMS.register("essence_stone", () -> new EssenceStoneItem(new Item.Properties().stacksTo(64)));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
