package net.alex.guzhenren.cultivation;

import net.alex.guzhenren.cultivation.attachments.*;
import net.alex.guzhenren.cultivation.enums.*;
import net.alex.guzhenren.network.NetworkHandler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

public class CultivationManager {
    private CultivationManager() {}

    /** 一个游戏日的 tick 数：1 day = 24000 ticks */
    public static final int TICKS_PER_DAY = 24000;

    public static BasicAttachments realm(Player player) {
        return player.getData(AttachmentTypes.REALM);
    }

    public static EssenceAttachment essence(Player player) {
        return player.getData(AttachmentTypes.ESSENCE);
    }

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

    /** 公式重算 max；current 自动 clamp 到新 max。仅写本地数据，不主动 sync */
    public static void recalculateMax(Player player) {
        BasicAttachments r = realm(player);
        EssenceAttachment e = essence(player);
        long newMax = computeMaxFromFormula(r);
        e.setMaxEssence(newMax);
        if (e.getCurrentEssence() > newMax) {
            e.setCurrentEssence(newMax);
        }
    }
    //endregion

    //region Level
    /** 开窍 (aperture opening)：抽天赋 + 范围内具体值，进入 RANK_1.INITIAL */
    public static boolean awaken(Player player, RandomSource random) {
        BasicAttachments r = realm(player);
        if (r.isAwaken()) return false;

        Aptitude aptitude = Aptitude.rollAptitude(random);
        int roll = aptitude.rollValue(random);

        r.setAwaken(true);
        r.setRank(Rank.RANK_1);
        r.setStage(Stage.INITIAL);

        // 走 setAptitudeRoll 入口，自动设 aptitude + recalc max
        setAptitudeRoll(player, roll);
        essence(player).setCurrentEssence(0L); // 开窍后初始 0 真元
        NetworkHandler.syncAll(player); // basic + essence 都变
        return true;
    }

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
        recalculateMax(player);
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
        recalculateMax(player);
        NetworkHandler.syncAll(player);
        return true;
    }
    //endregion

    //region Aptitude
    /**
     * 设置天赋占比 [0,100]
     * 自动同步 aptitude 等级 + 重算 max + 推送 sync
     * 用例: 改命道具直接指定占比、伤势降天赋
     */
    public static void setAptitudeRoll(Player player, int newRoll) {
        BasicAttachments r = realm(player);
        int clamped = Math.clamp(newRoll, 0, 100);
        r.setAptitudeRoll(clamped);
        r.setAptitude(aptitudeForRoll(clamped));
        recalculateMax(player);
        NetworkHandler.syncAll(player);
    }

    /**
     * 增减天赋占比 (delta 可正可负)
     * 跨 aptitude 区间时 aptitude 自动升/降
     * 用例: 类蛊虫道具 +10 占比、伤势 -2 占比
     */
    public static void addAptitudeRoll(Player player, int delta) {
        setAptitudeRoll(player, realm(player).getAptitudeRoll() + delta);
    }
    //endregion

    //region Essence
    /** 增加真元，自动 clamp 到 [0, max]；返回 clamp 后的新值 */
    public static long addEssence(Player player, long delta) {
        EssenceAttachment e = essence(player);
        long next = Math.clamp(e.getCurrentEssence() + delta, 0L, e.getMaxEssence());
        e.setCurrentEssence(next);
        NetworkHandler.syncEssence(player);
        return next;
    }

    /** 消耗真元；不足返回 false 且不扣减 */
    public static boolean consumeEssence(Player player, long cost) {
        if (cost <= 0L) return true;
        EssenceAttachment e = essence(player);
        if (e.getCurrentEssence() < cost) return false;
        e.setCurrentEssence(e.getCurrentEssence() - cost);
        NetworkHandler.syncEssence(player);
        return true;
    }

    /** 完全恢复 (起床用) */
    public static void restoreFull(Player player) {
        EssenceAttachment e = essence(player);
        e.setCurrentEssence(e.getMaxEssence());
        NetworkHandler.syncEssence(player);
    }
    //endregion

    //region Direct Setters - 仅供命令 / 管理操作使用 (debug & admin only)
    /** 直接设置转数；max 自动重算 */
    public static void setRank(Player player, Rank rank) {
        realm(player).setRank(rank);
        recalculateMax(player);
        NetworkHandler.syncAll(player);
    }

    /** 直接设置小境界；max 自动重算 */
    public static void setStage(Player player, Stage stage) {
        realm(player).setStage(stage);
        recalculateMax(player);
        NetworkHandler.syncAll(player);
    }

    /** 直接设置开窍状态；max 自动重算 */
    public static void setAwaken(Player player, boolean awaken) {
        realm(player).setAwaken(awaken);
        recalculateMax(player);
        NetworkHandler.syncAll(player);
    }

    /** 直接设置当前真元 (clamp 到 [0,max]) */
    public static void setCurrentEssence(Player player, long value) {
        EssenceAttachment e = essence(player);
        e.setCurrentEssence(Math.clamp(value, 0L, e.getMaxEssence()));
        NetworkHandler.syncEssence(player);
    }

    /** 重置为未开窍凡人状态 (主动重置；复用 onDeath 清零逻辑) */
    public static void reset(Player player) {
        onDeath(player, false); // 强制清零
        NetworkHandler.syncAll(player);
    }
    //endregion

    //region Tick
    /**
     * 每 tick 回血 (regen tick) - 仅 server side
     * 1 game day (24000 ticks) 从 0 → max；用 regenBuffer 避免整数舍入丢失
     * 只在 currentEssence 真正变化时才 sync，避免每 tick 发包
     */
    public static void tickRegen(Player player) {
        BasicAttachments r = realm(player);
        if (!r.isAwaken()) return;

        EssenceAttachment e = essence(player);
        long max = e.getMaxEssence();
        if (max <= 0L) return;
        long oldCurrent = e.getCurrentEssence();
        if (oldCurrent >= max) {
            e.setRegenBuffer(0.0);
            return;
        }

        double ratePerTick = (double) max / TICKS_PER_DAY;
        double buf = e.getRegenBuffer() + ratePerTick;
        if (buf >= 1.0) {
            long add = (long) buf;
            buf -= add;
            long next = Math.min(max, oldCurrent + add);
            e.setCurrentEssence(next);
        }
        e.setRegenBuffer(buf);

        // 仅在 currentEssence 真正变化时 sync
        if (e.getCurrentEssence() != oldCurrent) {
            NetworkHandler.syncEssence(player);
        }
    }
    //endregion

    //region Death
    /**
     * 死亡处理 - 由 keepInventory game rule 控制
     * keepInventory = true  → 全保留
     * keepInventory = false → 境界回 ORDINARY + 未开窍 + 真元全清零
     * sync 由 PlayerSyncEventHandler.onRespawn 处理，本方法不主动发包
     */
    public static void onDeath(Player player, boolean keepInventory) {
        if (keepInventory) return;

        BasicAttachments r = realm(player);
        r.setAwaken(false);
        r.setRank(Rank.ORDINARY);
        r.setStage(Stage.INITIAL);
        r.setAptitude(Aptitude.ORDINARY);
        r.setAptitudeRoll(0);

        recalculateMax(player); // max 重算为 0，current 自动 clamp
        EssenceAttachment e = essence(player);
        e.setCurrentEssence(0L);
        e.setRegenBuffer(0.0);
    }
    //endregion
}
