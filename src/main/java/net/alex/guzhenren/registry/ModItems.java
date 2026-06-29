package net.alex.guzhenren.registry;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.enums.core.Rank;
import net.alex.guzhenren.enums.gu.GuType;
import net.alex.guzhenren.enums.path.Path;
import net.alex.guzhenren.item.EssenceStoneItem;
import net.alex.guzhenren.item.gu.GuEffect;
import net.alex.guzhenren.item.gu.GuEffects;
import net.alex.guzhenren.item.gu.GuItem;
import net.alex.guzhenren.item.gu.GuProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Guzhenren.MOD_ID);

    //region MATERIALS
    public static final DeferredItem<EssenceStoneItem> ESSENCE_STONE =
            ITEMS.register("essence_stone", () -> new EssenceStoneItem(new Item.Properties().stacksTo(64)));
//endregion

    //region MORTAL GU - HUMAN PATH
    public static final DeferredItem<GuItem> HOPE_GU =
            registerMortalGu("hope_gu", Path.HUMAN, Rank.ONE, GuEffects.AWAKEN,
                    "hope_gu.use_success", "hope_gu.use_failed");
//endregion

    //region MORTAL GU - HEAVEN PATH (LIFESPAN)
    public static final DeferredItem<GuItem> LIFESPAN_GU           = registerLifespanGu("lifespan_gu",            1,  9);
    public static final DeferredItem<GuItem> TEN_YEARS_LIFESPAN_GU = registerLifespanGu("ten_years_lifespan_gu", 10, 99);
//endregion

    //region MORTAL GU - HEAVEN PATH (RELICS)
    public static final DeferredItem<GuItem> COPPER_RELICS_GU  = registerRelicsGu("copper_relics_gu",  Rank.ONE);
    public static final DeferredItem<GuItem> STEEL_RELICS_GU   = registerRelicsGu("steel_relics_gu",   Rank.TWO);
    public static final DeferredItem<GuItem> SILVER_RELICS_GU  = registerRelicsGu("silver_relics_gu",  Rank.THREE);
    public static final DeferredItem<GuItem> GOLD_RELICS_GU    = registerRelicsGu("gold_relics_gu",    Rank.FOUR);
    public static final DeferredItem<GuItem> CRYSTAL_RELICS_GU = registerRelicsGu("crystal_relics_gu", Rank.FIVE);
//endregion

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

//region HELPERS
    /** 注册一只通用一次性凡蛊 */
    private static DeferredItem<GuItem> registerMortalGu(
            String id, Path path, Rank rank, GuEffect effect, String successSuffix, String failSuffix) {
        return ITEMS.register(id, () -> new GuItem(
                new Item.Properties().stacksTo(64),
                GuProperties.builder().path(path).rank(rank).type(GuType.ONE_TIME).build(),
                effect,
                successSuffix != null ? "item.guzhenren." + successSuffix : null,
                failSuffix != null ? "item.guzhenren." + failSuffix : null
        ));
    }

    /**
     * 注册一只寿蛊 (天道一转, addLifespan effect, 共用 lifespan_gu.use_success message)
     * @param minYears 增寿年数下限 (闭区间)
     * @param maxYears 增寿年数上限 (闭区间)
     */
    private static DeferredItem<GuItem> registerLifespanGu(String id, int minYears, int maxYears) {
        return registerMortalGu(id, Path.HEAVEN, Rank.ONE, GuEffects.addLifespan(minYears, maxYears),
                "lifespan_gu.use_success", null);
    }

    /** 注册一只舍利蛊 (天道凡蛊, advanceStage effect, 共用 relics_gu.use_success message) */
    private static DeferredItem<GuItem> registerRelicsGu(String id, Rank rank) {
        return registerMortalGu(id, Path.HEAVEN, rank, GuEffects.advanceStage(rank),
                "relics_gu.use_success", null);
    }
//endregion
}
