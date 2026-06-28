package net.alex.guzhenren.gameplay.action;

import net.alex.guzhenren.gameplay.data.LifespanComponent;
import net.alex.guzhenren.registry.ModAttachments;
import net.alex.guzhenren.registry.ModDamageTypes;
import net.minecraft.server.level.ServerPlayer;

public class PlayerLifespanActions {

    public static void addMaxLifespan(ServerPlayer player, int amount) {
        player.getData(ModAttachments.PLAYER_DATA.get()).lifespan().addMaxLifespan(amount);
    }

    public static void subMaxLifespan(ServerPlayer player, int amount) {
        player.getData(ModAttachments.PLAYER_DATA.get()).lifespan().subMaxLifespan(amount);
    }

    public static void setAge(ServerPlayer player, int age) {
        player.getData(ModAttachments.PLAYER_DATA.get()).lifespan().setAge(age);
    }

    public static void reset(ServerPlayer player) {
        player.getData(ModAttachments.PLAYER_DATA.get()).lifespan().reset();
    }

    public static boolean checkAndKillIfDepleted(ServerPlayer player) {
        LifespanComponent lifespan = player.getData(ModAttachments.PLAYER_DATA.get()).lifespan();
        if (lifespan.getAge() < lifespan.getMaxLifespan()) return false;
        player.hurt(ModDamageTypes.lifespanDepleted(player.serverLevel()), Float.MAX_VALUE);
        return true;
    }
}
