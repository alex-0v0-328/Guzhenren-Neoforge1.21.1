package net.alex.guzhenren.gameplay.action;

import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.minecraft.server.level.ServerPlayer;

/** 玩家真元 (essence) 操作入口 */
public final class PlayerEssenceActions {

    private PlayerEssenceActions() {}

    public static void addEssence(ServerPlayer player, float amount) {
        ModPlayerData.of(player).essence().addCurrent(amount);
    }

    public static void subEssence(ServerPlayer player, float amount) {
        ModPlayerData.of(player).essence().subCurrent(amount);
    }

    public static void refillEssence(ServerPlayer player) {
        ModPlayerData.of(player).essence().refillCurrentEssence();
    }
}
