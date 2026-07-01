package net.alex.guzhenren.item;

import net.alex.guzhenren.gameplay.data.EssenceComponent;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 元石: 恢复真元 (max / USES_TO_REFILL). 未开窍 / essence 已满时 fail */
public class EssenceStoneItem extends AbstractModItem {

    /** 用 USES_TO_REFILL 次回满: 每次回复 = maxEssence / USES_TO_REFILL */
    private static final long USES_TO_REFILL = 20L;

    public EssenceStoneItem(Properties properties) {
        super(properties,
                null,
                null);
    }

    @Override
    protected ItemUseResult doUse(ServerPlayer sp, ItemStack stack) {
        ModPlayerData data = ModPlayerData.of(sp);

        if (!data.status().isApertureAwakened()) {
            return ItemUseResult.fail("item.guzhenren.essence_stone.use_failed.not_awakened");
        }

        EssenceComponent essence = data.essence();
        if (essence.getCurrentEssence() >= essence.getMaxEssence()) {
            return ItemUseResult.fail("item.guzhenren.essence_stone.use_failed.full");
        }

        long recovery = essence.getMaxEssence() / USES_TO_REFILL;
        essence.addCurrent(recovery);
        return ItemUseResult.succeed();
    }
}