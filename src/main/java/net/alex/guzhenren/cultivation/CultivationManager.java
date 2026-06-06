package net.alex.guzhenren.cultivation;

import net.alex.guzhenren.cultivation.attachments.*;
import net.alex.guzhenren.cultivation.enums.*;
import net.alex.guzhenren.network.NetworkHandler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

public class CultivationManager {

    private CultivationManager() {}

    //region Awaken - 跨系统
    /**
     * 开窍 (aperture opening)：抽天赋 + 范围内具体值，进入 RANK_1.INITIAL
     * 开窍后真元立刻满 (initial essence = max)
     */
    public static boolean awaken(Player player, RandomSource random) {
        BasicAttachments r = RealmManager.realm(player);
        if (r.isAwaken()) return false;

        Aptitude aptitude = Aptitude.rollAptitude(random);
        int roll = aptitude.rollValue(random);

        // basic 字段一次性写入 (避免中间状态发多包)
        r.setAwaken(true);
        r.setRank(Rank.RANK_1);
        r.setStage(Stage.INITIAL);
        r.setAptitude(aptitude);
        r.setAptitudeRoll(roll);

        // essence: 重算 max 后真元立刻满
        EssenceManager.recalculateMax(player);
        EssenceAttachment e = EssenceManager.essence(player);
        e.setCurrentEssence(e.getMaxEssence());

        NetworkHandler.syncAll(player);
        return true;
    }
    //endregion

    //region Reset / Death - 跨系统
    /** 主动重置 (debug 命令 / 改命道具用)；复用 onDeath 清零 + 强制同步 */
    public static void reset(Player player) {
        onDeath(player, false);
        NetworkHandler.syncAll(player);
    }

    /**
     * 死亡处理 - 由 keepInventory game rule 控制
     * keepInventory = true  → 全保留
     * keepInventory = false → 境界回 ORDINARY + 未开窍 + 真元全清零
     * sync 由 PlayerSyncEventHandler.onRespawn 统一推送，本方法不主动发包
     */
    public static void onDeath(Player player, boolean keepInventory) {
        if (keepInventory) return;

        BasicAttachments r = RealmManager.realm(player);
        r.setAwaken(false);
        r.setRank(Rank.ORDINARY);
        r.setStage(Stage.INITIAL);
        r.setAptitude(Aptitude.ORDINARY);
        r.setAptitudeRoll(0);

        EssenceManager.recalculateMax(player); // max 重算为 0，current 自动 clamp
        EssenceAttachment e = EssenceManager.essence(player);
        e.setCurrentEssence(0L);
        e.setRegenBuffer(0.0);
    }
    //endregion
}
