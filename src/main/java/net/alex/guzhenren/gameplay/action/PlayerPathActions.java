package net.alex.guzhenren.gameplay.action;

import net.alex.guzhenren.enums.path.Attainment;
import net.alex.guzhenren.enums.path.Path;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.minecraft.server.level.ServerPlayer;

/** 玩家流派 (path) 操作入口: marks 与 attainment */
public final class PlayerPathActions {

    private PlayerPathActions() {}

    private static final Attainment[] ATTAINMENT_ORDER = Attainment.values();

//region MARKS
    public static void addMarks(ServerPlayer player, Path path, long amount) {
        ModPlayerData.of(player).path().addMarks(path, amount);
    }

    public static void subMarks(ServerPlayer player, Path path, long amount) {
        ModPlayerData.of(player).path().subMarks(path, amount);
    }
//endregion

//region ATTAINMENT
    public static void setAttainment(ServerPlayer player, Path path, Attainment attainment) {
        ModPlayerData.of(player).path().setAttainment(path, attainment);
    }

    /** 升 attainment 一档. @return false 若已是 SUPREME_GRANDMASTER */
    public static boolean attainmentUp(ServerPlayer player, Path path) {
        ModPlayerData data = ModPlayerData.of(player);
        Attainment cur = data.path().getAttainment(path);
        int idx = cur.ordinal();
        if (idx >= ATTAINMENT_ORDER.length - 1) return false;
        data.path().setAttainment(path, ATTAINMENT_ORDER[idx + 1]);
        return true;
    }

    /** 降 attainment 一档. @return false 若已是 ORDINARY */
    public static boolean attainmentDown(ServerPlayer player, Path path) {
        ModPlayerData data = ModPlayerData.of(player);
        Attainment cur = data.path().getAttainment(path);
        int idx = cur.ordinal();
        if (idx == 0) return false;
        data.path().setAttainment(path, ATTAINMENT_ORDER[idx - 1]);
        return true;
    }
//endregion
}
