package net.alex.guzhenren.gameplay.action;

import net.alex.guzhenren.gameplay.data.SoulComponent;
import net.alex.guzhenren.registry.ModAttachments;
import net.alex.guzhenren.registry.ModDamageTypes;
import net.minecraft.server.level.ServerPlayer;

public class PlayerSoulActions {

    public static void addSoul(ServerPlayer player, long amount) {
        player.getData(ModAttachments.PLAYER_DATA.get()).soul().addSoul(amount);
    }

    public static void subSoul(ServerPlayer player, long amount) {
        player.getData(ModAttachments.PLAYER_DATA.get()).soul().subSoul(amount);
    }

    public static void reset(ServerPlayer player) {
        player.getData(ModAttachments.PLAYER_DATA.get()).soul().reset();
    }

    /** 检查 soul 是否衰竭, 若是则触发死亡 */
    public static boolean checkAndKillIfCollapsed(ServerPlayer player) {
        SoulComponent soul = player.getData(ModAttachments.PLAYER_DATA.get()).soul();
        if (soul.getSoul() > 0L) return false;
        player.hurt(ModDamageTypes.soulCollapsed(player.serverLevel()), Float.MAX_VALUE);
        return true;
    }
}
