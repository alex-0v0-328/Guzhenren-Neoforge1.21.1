package net.alex.guzhenren.gameplay.action;

import net.alex.guzhenren.registry.ModAttachment;
import net.minecraft.server.level.ServerPlayer;

public class PlayerEssenceActions {

    public static void addEssence(ServerPlayer player, float amount) {
        player.getData(ModAttachment.PLAYER_DATA.get()).essence().addCurrent(amount);
    }

    public static void subEssence(ServerPlayer player, float amount) {
        player.getData(ModAttachment.PLAYER_DATA.get()).essence().subCurrent(amount);
    }

    public static void refillEssence(ServerPlayer player) {
        player.getData(ModAttachment.PLAYER_DATA.get()).essence().refillCurrentEssence();
    }
}
