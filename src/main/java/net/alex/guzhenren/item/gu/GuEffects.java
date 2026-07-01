package net.alex.guzhenren.item.gu;

import java.util.concurrent.ThreadLocalRandom;
import net.alex.guzhenren.enums.core.Rank;
import net.alex.guzhenren.enums.core.Stage;
import net.alex.guzhenren.gameplay.action.PlayerCoreActions;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.item.ItemUseResult;
import net.minecraft.network.chat.Component;

public class GuEffects {

    public static final GuEffect AWAKEN = player -> {
        ModPlayerData data = ModPlayerData.of(player);
        if (!PlayerCoreActions.canAwaken(data)) return ItemUseResult.fail();
        PlayerCoreActions.awaken(player);
        return ItemUseResult.succeed();
    };

    /**
     * 增加寿元 factory: 随机 [minYears, maxYears] (闭区间)
     * 用于寿蛊系列, message arg 为 roll 出的年数
     */
    public static GuEffect addLifespan(int minYears, int maxYears) {
        return player -> {
            int years = ThreadLocalRandom.current().nextInt(minYears, maxYears + 1);
            ModPlayerData.of(player).lifespan().addMaxLifespan(years);
            return ItemUseResult.succeed(years);
        };
    }

    /**
     * 提升 stage 修为. 蛊虫 rank 必须等于玩家 rank, 且玩家未达 PEAK
     */
    public static GuEffect advanceStage(Rank requiredRank) {
        return player -> {
            ModPlayerData data = ModPlayerData.of(player);
            if (data.core().getPlayerRank() != requiredRank) {
                return ItemUseResult.fail("item.guzhenren.relics_gu.use_failed.rank_mismatch");
            }
            if (!PlayerCoreActions.stageUp(player)) {
                return ItemUseResult.fail("item.guzhenren.relics_gu.use_failed.stage_peak");
            }
            Stage newStage = data.core().getPlayerStage();
            return ItemUseResult.succeed(Component.translatable(newStage.getTranslationKey()));
        };
    }
}
