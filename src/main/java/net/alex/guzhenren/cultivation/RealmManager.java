package net.alex.guzhenren.cultivation;

import net.alex.guzhenren.cultivation.attachments.BasicAttachments;
import net.alex.guzhenren.cultivation.enums.*;
import net.alex.guzhenren.network.NetworkHandler;
import net.minecraft.world.entity.player.Player;

public final class RealmManager {

    private RealmManager() {}

    //region Data Access
    public static BasicAttachments realm(Player player) {
        return player.getData(AttachmentTypes.REALM);
    }
    //endregion

    //region Formulas
    /** roll → aptitude 等级反查 (含 EXTREME=100 特例) */
    public static Aptitude aptitudeForRoll(int roll) {
        if (roll >= 100) return Aptitude.EXTREME;
        if (roll >= 80)  return Aptitude.FIRST;
        if (roll >= 60)  return Aptitude.SECOND;
        if (roll >= 40)  return Aptitude.THIRD;
        if (roll >= 20)  return Aptitude.FOURTH;
        return Aptitude.ORDINARY; // 失常区间 (<20)：保留开窍状态，但占比退化
    }

    /**
     * 公式: rank.baseEssence × stage.stageMultiplier × roll / 100
     * 未开窍 / ORDINARY rank / roll<=0 → 0
     */
    public static long computeMaxFromFormula(BasicAttachments r) {
        if (!r.isAwaken()) return 0L;
        if (r.getRank() == Rank.ORDINARY) return 0L;
        if (r.getAptitudeRoll() <= 0) return 0L;
        long base = (long) r.getRank().getBaseEssence() * r.getStage().getStageMultiplier();
        return base * r.getAptitudeRoll() / 100L;
    }
    //endregion

    //region Level
    /** 升小境界 (advance stage)；PEAK 时返回 false，需调 advanceRank */
    public static boolean advanceStage(Player player) {
        BasicAttachments r = realm(player);
        if (!r.isAwaken() || r.getRank() == Rank.ORDINARY) return false;

        Stage next = switch (r.getStage()) {
            case INITIAL -> Stage.MIDDLE;
            case MIDDLE  -> Stage.UPPER;
            case UPPER   -> Stage.PEAK;
            case PEAK    -> null;
        };
        if (next == null) return false;
        r.setStage(next);
        EssenceManager.recalculateMax(player);
        NetworkHandler.syncAll(player);
        return true;
    }

    /** 升大境界 (advance rank)；小境界自动重置为 INITIAL */
    public static boolean advanceRank(Player player) {
        BasicAttachments r = realm(player);
        if (!r.isAwaken()) return false;

        Rank next = switch (r.getRank()) {
            case ORDINARY -> Rank.RANK_1;
            case RANK_1   -> Rank.RANK_2;
            case RANK_2   -> Rank.RANK_3;
            case RANK_3   -> Rank.RANK_4;
            case RANK_4   -> Rank.RANK_5;
            case RANK_5   -> null;
        };
        if (next == null) return false;
        r.setRank(next);
        r.setStage(Stage.INITIAL);
        EssenceManager.recalculateMax(player);
        NetworkHandler.syncAll(player);
        return true;
    }
    //endregion

    //region Aptitude
    /**
     * 设置天赋占比 [0,100]
     * 自动同步 aptitude 等级 + 重算 max + 推送 sync
     */
    public static void setAptitudeRoll(Player player, int newRoll) {
        BasicAttachments r = realm(player);
        int clamped = Math.clamp(newRoll, 0, 100);
        r.setAptitudeRoll(clamped);
        r.setAptitude(aptitudeForRoll(clamped));
        EssenceManager.recalculateMax(player);
        NetworkHandler.syncAll(player);
    }

    /** 增减天赋占比 (delta 可正可负)；跨 aptitude 区间时 aptitude 自动升/降 */
    public static void addAptitudeRoll(Player player, int delta) {
        setAptitudeRoll(player, realm(player).getAptitudeRoll() + delta);
    }
    //endregion

    //region Direct Setters - 仅供命令 / 管理操作 (debug & admin only)
    /** 直接设置转数；max 自动重算 */
    public static void setRank(Player player, Rank rank) {
        realm(player).setRank(rank);
        EssenceManager.recalculateMax(player);
        NetworkHandler.syncAll(player);
    }

    /** 直接设置小境界；max 自动重算 */
    public static void setStage(Player player, Stage stage) {
        realm(player).setStage(stage);
        EssenceManager.recalculateMax(player);
        NetworkHandler.syncAll(player);
    }

    /** 直接设置开窍状态；max 自动重算 */
    public static void setAwaken(Player player, boolean awaken) {
        realm(player).setAwaken(awaken);
        EssenceManager.recalculateMax(player);
        NetworkHandler.syncAll(player);
    }
    //endregion
}
