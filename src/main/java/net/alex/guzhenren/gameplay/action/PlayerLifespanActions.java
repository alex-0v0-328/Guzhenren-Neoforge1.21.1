package net.alex.guzhenren.gameplay.action;

import net.alex.guzhenren.gameplay.data.LifespanComponent;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.gameplay.sync.SyncAudit;
import net.alex.guzhenren.registry.ModDamageTypes;
import net.minecraft.server.level.ServerPlayer;

/** 玩家寿元 (lifespan) 操作入口. 仅保留含 hurt 触发的 business logic */
public final class PlayerLifespanActions {

    private PlayerLifespanActions() {}

    /** 检查寿元是否耗尽, 若是则打死因 flag + 触发死亡. @return true 若已触发死亡 */
    public static boolean checkAndKillIfDepleted(ServerPlayer player) {
        ModPlayerData data = ModPlayerData.of(player);
        LifespanComponent lifespan = data.lifespan();
        if (lifespan.getAge() < lifespan.getMaxLifespan()) return false;
        data.status().markDeathByLifespanOrSoul();
        SyncAudit.log("hurt.lifespan", player);
        player.hurt(ModDamageTypes.lifespanDepleted(player.serverLevel()), Float.MAX_VALUE);
        return true;
    }
}