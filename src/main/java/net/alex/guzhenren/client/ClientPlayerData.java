package net.alex.guzhenren.client;

import net.alex.guzhenren.gameplay.data.PlayerData;
import net.alex.guzhenren.gameplay.data.*;

public class ClientPlayerData {

    private static CultivationData cultivation = new CultivationData();
    private static EssenceData essence = new EssenceData();
    private static StatusData status = new StatusData();
    private static PathData path = new PathData();

    public static CultivationData getCultivation() { return cultivation; }
    public static EssenceData getEssence() { return essence; }
    public static StatusData getStatus() { return status; }
    public static PathData getPath() { return path; }

    public static void setCultivation(CultivationData c) { cultivation = c; }
    public static void setEssence(EssenceData e) { essence = e; }
    public static void setStatus(StatusData s) { status = s; }
    public static void setPath(PathData p) { path = p; }

    public static void setAll(PlayerData data) {
        cultivation = data.cultivation();
        essence = data.essence();
        status = data.status();
        path = data.path();
    }
}
