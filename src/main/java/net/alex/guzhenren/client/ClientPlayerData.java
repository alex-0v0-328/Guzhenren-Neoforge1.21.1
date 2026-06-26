package net.alex.guzhenren.client;

import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.gameplay.data.*;

public class ClientPlayerData {

    private static CoreComponent cultivation = new CoreComponent();
    private static EssenceComponent essence = new EssenceComponent();
    private static StatusComponent status = new StatusComponent();
    private static PathComponent path = new PathComponent();

    public static CoreComponent getCultivation() { return cultivation; }
    public static EssenceComponent getEssence() { return essence; }
    public static StatusComponent getStatus() { return status; }
    public static PathComponent getPath() { return path; }

    public static void setCultivation(CoreComponent c) { cultivation = c; }
    public static void setEssence(EssenceComponent e) { essence = e; }
    public static void setStatus(StatusComponent s) { status = s; }
    public static void setPath(PathComponent p) { path = p; }

    public static void setAll(ModPlayerData data) {
        cultivation = data.cultivation();
        essence = data.essence();
        status = data.status();
        path = data.path();
    }
}
