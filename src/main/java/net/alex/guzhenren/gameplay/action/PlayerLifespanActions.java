package net.alex.guzhenren.gameplay.action;

import net.alex.guzhenren.gameplay.data.LifespanComponent;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.registry.ModDamageTypes;
import net.minecraft.server.level.ServerPlayer;

/** 玩家寿元 (lifespan) 操作入口 */
public final class PlayerLifespanActions {

    private PlayerLifespanActions() {}

    public static void addMaxLifespan(ServerPlayer player, int amount) {
        ModPlayerData.of(player).lifespan().addMaxLifespan(amount);
    }

    public static void subMaxLifespan(ServerPlayer player, int amount) {
        ModPlayerData.of(player).lifespan().subMaxLifespan(amount);
    }

    public static void setAge(ServerPlayer player, int age) {
        ModPlayerData.of(player).lifespan().setAge(age);
    }

    public static void reset(ServerPlayer player) {
        ModPlayerData.of(player).lifespan().reset();
    }

    /** 检查寿元是否耗尽, 若是则触发死亡. @return true 若已触发死亡 */
    public static boolean checkAndKillIfDepleted(ServerPlayer player) {
        LifespanComponent lifespan = ModPlayerData.of(player).lifespan();
        if (lifespan.getAge() < lifespan.getMaxLifespan()) return false;
        player.hurt(ModDamageTypes.lifespanDepleted(player.serverLevel()), Float.MAX_VALUE);
        return true;
    }
}
