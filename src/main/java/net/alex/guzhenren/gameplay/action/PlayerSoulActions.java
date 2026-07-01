package net.alex.guzhenren.gameplay.action;

import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.gameplay.data.SoulComponent;
import net.alex.guzhenren.gameplay.sync.SyncAudit;
import net.alex.guzhenren.registry.ModDamageTypes;
import net.minecraft.server.level.ServerPlayer;

/** 玩家魂魄 (soul) 操作入口. 仅保留含 hurt 触发的 business logic */
public final class PlayerSoulActions {

    private PlayerSoulActions() {}

    /** 检查魂魄是否衰竭, 若是则打死因 flag + 触发死亡. @return true 若已触发死亡 */
    public static boolean checkAndKillIfCollapsed(ServerPlayer player) {
        ModPlayerData data = ModPlayerData.of(player);
        SoulComponent soul = data.soul();
        if (soul.getSoul() > 0L) return false;
        data.status().markDeathByLifespanOrSoul();
        SyncAudit.log("hurt.soul", player);
        player.hurt(ModDamageTypes.soulCollapsed(player.serverLevel()), Float.MAX_VALUE);
        return true;
    }
}