package net.alex.guzhenren.gameplay.action;

import java.util.List;
import net.alex.guzhenren.enums.core.Rank;
import net.alex.guzhenren.enums.core.Stage;
import net.alex.guzhenren.enums.core.Talent;
import net.alex.guzhenren.enums.core.TenExtreme;
import net.alex.guzhenren.gameplay.data.CoreComponent;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.minecraft.server.level.ServerPlayer;

/** 玩家核心修为 (rank / stage / talent / physique / baseEssence) 操作入口 */
public final class PlayerCoreActions {

    private PlayerCoreActions() {}

    private static final List<Rank> RANK_ORDER = List.of(Rank.ONE, Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE);
    private static final List<Stage> STAGE_ORDER = List.of(Stage.INIT, Stage.MIDDLE, Stage.UPPER, Stage.PEAK);

//region PRECONDITION
    /** 检查是否可开窍 (未开窍 + 所有 core 字段为初始值) */
    public static boolean canAwaken(ModPlayerData data) {
        return !data.status().isApertureAwakened()
                && data.core().getPlayerTalent() == Talent.NONE
                && data.core().getPlayerRank() == Rank.MORTAL
                && data.core().getPlayerStage() == Stage.NONE
                && data.core().getPlayerExtremePhysique() == TenExtreme.NONE;
    }

    public static boolean isAwakened(ModPlayerData data) {
        return data.status().isApertureAwakened();
    }
//endregion

//region AWAKEN
    /** 随机 talent + 随机 percent 开窍 */
    public static void awaken(ServerPlayer player) {
        awaken(player, Talent.randomNonNone());
    }

    /** 指定 talent, 随机 percent 开窍 */
    public static void awaken(ServerPlayer player, Talent talent) {
        awaken(player, talent, Talent.randomPercent(talent));
    }

    /** 指定 talent + percent 开窍 (核心实现) */
    public static void awaken(ServerPlayer player, Talent talent, int percent) {
        ModPlayerData data = ModPlayerData.of(player);
        CoreComponent c = data.core();
        c.setPlayerTalent(talent);
        c.setPlayerBaseEssence(percent);
        c.setPlayerRank(Rank.ONE);
        c.setPlayerStage(Stage.INIT);
        c.setPlayerExtremePhysique(talent == Talent.TEN_EXTREME ? TenExtreme.randomNonNone() : TenExtreme.NONE);
        data.status().setApertureAwakened(true);
        recomputeMax(data);
        data.essence().refillCurrentEssence();
    }
//endregion

//region RANK / STAGE
    /** @return false 若已是最高 rank */
    public static boolean rankUp(ServerPlayer player) {
        ModPlayerData data = ModPlayerData.of(player);
        int idx = RANK_ORDER.indexOf(data.core().getPlayerRank());
        if (idx == -1 || idx >= RANK_ORDER.size() - 1) return false;
        setRank(player, RANK_ORDER.get(idx + 1));
        return true;
    }

    /** @return false 若已是最低 rank */
    public static boolean rankDown(ServerPlayer player) {
        ModPlayerData data = ModPlayerData.of(player);
        int idx = RANK_ORDER.indexOf(data.core().getPlayerRank());
        if (idx <= 0) return false;
        setRank(player, RANK_ORDER.get(idx - 1));
        return true;
    }

    public static void setRank(ServerPlayer player, Rank rank) {
        ModPlayerData data = ModPlayerData.of(player);
        data.core().setPlayerRank(rank);
        recomputeMax(data);
    }

    /** @return false 若已是 PEAK stage */
    public static boolean stageUp(ServerPlayer player) {
        ModPlayerData data = ModPlayerData.of(player);
        int idx = STAGE_ORDER.indexOf(data.core().getPlayerStage());
        if (idx == -1 || idx >= STAGE_ORDER.size() - 1) return false;
        setStage(player, STAGE_ORDER.get(idx + 1));
        return true;
    }

    /** @return false 若已是 INIT stage */
    public static boolean stageDown(ServerPlayer player) {
        ModPlayerData data = ModPlayerData.of(player);
        int idx = STAGE_ORDER.indexOf(data.core().getPlayerStage());
        if (idx <= 0) return false;
        setStage(player, STAGE_ORDER.get(idx - 1));
        return true;
    }

    public static void setStage(ServerPlayer player, Stage stage) {
        ModPlayerData data = ModPlayerData.of(player);
        data.core().setPlayerStage(stage);
        recomputeMax(data);
    }
//endregion

//region TALENT / BASE / PHYSIQUE
    public static void setTalent(ServerPlayer player, Talent talent) {
        ModPlayerData data = ModPlayerData.of(player);
        CoreComponent c = data.core();
        c.setPlayerTalent(talent);
        c.setPlayerExtremePhysique(talent == Talent.TEN_EXTREME ? TenExtreme.randomNonNone() : TenExtreme.NONE);
        c.setPlayerBaseEssence(Talent.randomPercent(talent));
        recomputeMax(data);
    }

    public static void addBaseEssence(ServerPlayer player, int amount) {
        ModPlayerData data = ModPlayerData.of(player);
        CoreComponent c = data.core();
        c.addPlayerBaseEssence(amount);
        syncTalentFromBaseEssence(c);
        ensurePhysiqueIfTenExtreme(data);
        recomputeMax(data);
    }

    public static void subBaseEssence(ServerPlayer player, int amount) {
        ModPlayerData data = ModPlayerData.of(player);
        CoreComponent c = data.core();
        c.subPlayerBaseEssence(amount);
        syncTalentFromBaseEssence(c);
        ensurePhysiqueIfTenExtreme(data);
        recomputeMax(data);
    }

    public static void setPhysique(ServerPlayer player, TenExtreme physique) {
        ModPlayerData data = ModPlayerData.of(player);
        CoreComponent c = data.core();
        c.setPlayerTalent(Talent.TEN_EXTREME);
        c.setPlayerBaseEssence(100);
        c.setPlayerExtremePhysique(physique);
        recomputeMax(data);
    }
//endregion

//region RESET
    /** 全部数据重置. 委托给 ModPlayerData.reset() */
    public static void reset(ServerPlayer player) {
        ModPlayerData.of(player).reset();
    }
//endregion

//region HELPERS
    private static void recomputeMax(ModPlayerData data) {
        data.essence().recomputeMaxEssence(
                data.core().getPlayerBaseEssence(),
                data.core().getPlayerRank(),
                data.core().getPlayerStage()
        );
    }

    /** 根据 baseEssence 反查 talent. 若 talent 变化则同步 + 清 physique (若非 TEN_EXTREME) */
    private static void syncTalentFromBaseEssence(CoreComponent c) {
        Talent inferred = Talent.fromPercent(c.getPlayerBaseEssence());
        if (inferred == c.getPlayerTalent()) return;
        c.setPlayerTalent(inferred);
        if (inferred != Talent.TEN_EXTREME) c.setPlayerExtremePhysique(TenExtreme.NONE);
    }

    private static void ensurePhysiqueIfTenExtreme(ModPlayerData data) {
        CoreComponent c = data.core();
        if (c.getPlayerTalent() == Talent.TEN_EXTREME
                && c.getPlayerExtremePhysique() == TenExtreme.NONE) {
            c.setPlayerExtremePhysique(TenExtreme.randomNonNone());
        }
    }
//endregion
}
