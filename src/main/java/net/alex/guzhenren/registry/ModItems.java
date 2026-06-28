package net.alex.guzhenren.registry;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.enums.core.Rank;
import net.alex.guzhenren.enums.gu.GuType;
import net.alex.guzhenren.enums.path.Path;
import net.alex.guzhenren.item.EssenceStoneItem;
import net.alex.guzhenren.item.gu.GuEffects;
import net.alex.guzhenren.item.gu.GuItem;
import net.alex.guzhenren.item.gu.GuProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Guzhenren.MOD_ID);

    public static final DeferredItem<EssenceStoneItem> ESSENCE_STONE =
            ITEMS.register("essence_stone", () -> new EssenceStoneItem(new Item.Properties().stacksTo(64)));

    public static final DeferredItem<GuItem> HOPE_GU =
            ITEMS.register("hope_gu", () -> new GuItem(
                    new Item.Properties().stacksTo(64),
                    GuProperties.builder()
                            .path(Path.HUMAN)
                            .rank(Rank.ONE)
                            .type(GuType.ONE_TIME)
                            .build(),
                    GuEffects.AWAKEN,
                    "item.guzhenren.hope_gu.use_success",
                    "item.guzhenren.hope_gu.use_failed"
            ));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
