package net.alex.guzhenren.gameplay.action;

import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.gameplay.data.SoulComponent;
import net.alex.guzhenren.registry.ModDamageTypes;
import net.minecraft.server.level.ServerPlayer;

/** 玩家魂魄 (soul) 操作入口 */
public final class PlayerSoulActions {

    private PlayerSoulActions() {}

    public static void addSoul(ServerPlayer player, long amount) {
        ModPlayerData.of(player).soul().addSoul(amount);
    }

    public static void subSoul(ServerPlayer player, long amount) {
        ModPlayerData.of(player).soul().subSoul(amount);
    }

    public static void reset(ServerPlayer player) {
        ModPlayerData.of(player).soul().reset();
    }

    /** 检查魂魄是否衰竭, 若是则触发死亡. @return true 若已触发死亡 */
    public static boolean checkAndKillIfCollapsed(ServerPlayer player) {
        SoulComponent soul = ModPlayerData.of(player).soul();
        if (soul.getSoul() > 0L) return false;
        player.hurt(ModDamageTypes.soulCollapsed(player.serverLevel()), Float.MAX_VALUE);
        return true;
    }
}
