package net.alex.guzhenren.item.gu;

import java.util.concurrent.ThreadLocalRandom;
import net.alex.guzhenren.enums.core.Rank;
import net.alex.guzhenren.enums.core.Stage;
import net.alex.guzhenren.gameplay.action.PlayerCoreActions;
import net.alex.guzhenren.gameplay.action.PlayerLifespanActions;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.registry.ModAttachments;
import net.minecraft.network.chat.Component;

public class GuEffects {

    public static final GuEffect AWAKEN = player -> {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        if (!PlayerCoreActions.canAwaken(data)) return GuEffectResult.fail();
        PlayerCoreActions.awaken(player);
        return GuEffectResult.succeed();
    };

    public static final GuEffect ADD_LIFESPAN_1_9 = player -> {
        int years = ThreadLocalRandom.current().nextInt(1, 10);
        PlayerLifespanActions.addMaxLifespan(player, years);
        return GuEffectResult.succeed(years);
    };

    public static final GuEffect ADD_LIFESPAN_10_99 = player -> {
        int years = ThreadLocalRandom.current().nextInt(10, 100);
        PlayerLifespanActions.addMaxLifespan(player, years);
        return GuEffectResult.succeed(years);
    };

    /**
     * 提升 stage 修为. 蛊虫 rank 必须等于玩家 rank, 且玩家未达 PEAK
     */
    public static GuEffect advanceStage(Rank requiredRank) {
        return player -> {
            ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
            if (data.core().getPlayerRank() != requiredRank) {
                return GuEffectResult.fail("item.guzhenren.relics_gu.use_failed.rank_mismatch");
            }
            if (data.core().getPlayerStage() == Stage.PEAK) {
                return GuEffectResult.fail("item.guzhenren.relics_gu.use_failed.stage_peak");
            }
            if (!PlayerCoreActions.stageUp(player)) {
                return GuEffectResult.fail("item.guzhenren.relics_gu.use_failed.stage_peak");
            }
            Stage newStage = data.core().getPlayerStage();
            return GuEffectResult.succeed(Component.translatable(newStage.getTranslationKey()));
        };
    }
}
