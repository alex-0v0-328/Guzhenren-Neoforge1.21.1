package net.alex.guzhenren.cultivation;

import net.alex.guzhenren.cultivation.attachments.BasicAttachments;
import net.alex.guzhenren.cultivation.attachments.EssenceAttachment;
import net.alex.guzhenren.network.NetworkHandler;
import net.minecraft.world.entity.player.Player;

public final class EssenceManager {

    private EssenceManager() {}

    /** 一个游戏日的 tick 数：1 day = 24000 ticks */
    public static final int TICKS_PER_DAY = 24000;

    //region Data Access
    public static EssenceAttachment essence(Player player) {
        return player.getData(AttachmentTypes.ESSENCE);
    }
    //endregion

    //region Max Recalc
    /**
     * 根据 RealmManager 公式重算 max；current 自动 clamp 到新 max
     * 注意: 仅写本地数据，不主动 sync — 由调用方负责发包
     */
    public static void recalculateMax(Player player) {
        BasicAttachments r = RealmManager.realm(player);
        EssenceAttachment e = essence(player);
        long newMax = RealmManager.computeMaxFromFormula(r);
        e.setMaxEssence(newMax);
        if (e.getCurrentEssence() > newMax) {
            e.setCurrentEssence(newMax);
        }
    }
    //endregion

    //region Essence Ops
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

    /** 直接设置当前真元 (clamp 到 [0,max])；debug 用 */
    public static void setCurrentEssence(Player player, long value) {
        EssenceAttachment e = essence(player);
        e.setCurrentEssence(Math.clamp(value, 0L, e.getMaxEssence()));
        NetworkHandler.syncEssence(player);
    }
    //endregion

    //region Tick
    /**
     * 每 tick 回血 (regen tick) - 仅 server side
     * 1 game day (24000 ticks) 从 0 → max；用 regenBuffer 避免整数舍入丢失
     * 只在 currentEssence 真正变化时才 sync，避免每 tick 发包
     */
    public static void tickRegen(Player player) {
        BasicAttachments r = RealmManager.realm(player);
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

        if (e.getCurrentEssence() != oldCurrent) {
            NetworkHandler.syncEssence(player);
        }
    }
    //endregion
}
