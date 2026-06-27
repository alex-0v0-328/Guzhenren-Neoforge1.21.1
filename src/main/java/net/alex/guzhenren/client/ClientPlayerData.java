package net.alex.guzhenren.client;

import net.alex.guzhenren.gameplay.data.CoreComponent;
import net.alex.guzhenren.gameplay.data.EssenceComponent;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.gameplay.data.PathComponent;
import net.alex.guzhenren.gameplay.data.StatusComponent;

public class ClientPlayerData {

    private static CoreComponent core = new CoreComponent();
    private static EssenceComponent essence = new EssenceComponent();
    private static StatusComponent status = new StatusComponent();
    private static PathComponent path = new PathComponent();

    //region GETTER
    public static CoreComponent getCore() { return core; }
    public static EssenceComponent getEssence() { return essence; }
    public static StatusComponent getStatus() { return status; }
    public static PathComponent getPath() { return path; }
//endregion

    //region SETTER
    public static void setCore(CoreComponent core) { ClientPlayerData.core = core; }
    public static void setEssence(EssenceComponent essence) { ClientPlayerData.essence = essence; }
    public static void setStatus(StatusComponent status) { ClientPlayerData.status = status; }
    public static void setPath(PathComponent path) { ClientPlayerData.path = path; }
//endregion

    public static void setAll(ModPlayerData data) {
        core = data.core();
        essence = data.essence();
        status = data.status();
        path = data.path();
    }
}
