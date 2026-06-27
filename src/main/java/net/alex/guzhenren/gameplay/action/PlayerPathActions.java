package net.alex.guzhenren.gameplay.action;

import net.alex.guzhenren.enums.path.Attainment;
import net.alex.guzhenren.enums.path.Path;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;

public class PlayerPathActions {

    private static final Attainment[] ATTAINMENT_ORDER = Attainment.values();

    //region MARKS
    public static void addMarks(ServerPlayer player, Path path, long amount) {
        player.getData(ModAttachments.PLAYER_DATA.get()).path().addMarks(path, amount);
    }

    public static void subMarks(ServerPlayer player, Path path, long amount) {
        player.getData(ModAttachments.PLAYER_DATA.get()).path().subMarks(path, amount);
    }
    //endregion

    //region ATTAINMENT
    public static void setAttainment(ServerPlayer player, Path path, Attainment attainment) {
        player.getData(ModAttachments.PLAYER_DATA.get()).path().setAttainment(path, attainment);
    }

    /**
     * 升 attainment, 已 SUPREME_GRANDMASTER 时 fail
     */
    public static boolean attainmentUp(ServerPlayer player, Path path) {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        Attainment cur = data.path().getAttainment(path);
        int idx = cur.ordinal();
        if (idx >= ATTAINMENT_ORDER.length - 1) return false;
        data.path().setAttainment(path, ATTAINMENT_ORDER[idx + 1]);
        return true;
    }

    /**
     * 降 attainment, 已 ORDINARY 时 fail
     */
    public static boolean attainmentDown(ServerPlayer player, Path path) {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        Attainment cur = data.path().getAttainment(path);
        int idx = cur.ordinal();
        if (idx == 0) return false;
        data.path().setAttainment(path, ATTAINMENT_ORDER[idx - 1]);
        return true;
    }
    //endregion
}
