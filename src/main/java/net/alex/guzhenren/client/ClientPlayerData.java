package net.alex.guzhenren.client;

import java.util.List;
import net.alex.guzhenren.gameplay.data.CoreComponent;
import net.alex.guzhenren.gameplay.data.EssenceComponent;
import net.alex.guzhenren.gameplay.data.LifespanComponent;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.gameplay.data.PathComponent;
import net.alex.guzhenren.gameplay.data.SoulComponent;
import net.alex.guzhenren.gameplay.data.StatusComponent;
import net.alex.guzhenren.network.sync.PathDelta;

public class ClientPlayerData {

    private static CoreComponent core = new CoreComponent();
    private static EssenceComponent essence = new EssenceComponent();
    private static StatusComponent status = new StatusComponent();
    private static PathComponent path = new PathComponent();
    private static LifespanComponent lifespan = new LifespanComponent();
    private static SoulComponent soul = new SoulComponent();

    //region GETTER
    public static CoreComponent getCore() { return core; }
    public static EssenceComponent getEssence() { return essence; }
    public static StatusComponent getStatus() { return status; }
    public static PathComponent getPath() { return path; }
    public static LifespanComponent getLifespan() { return lifespan; }
    public static SoulComponent getSoul() { return soul; }
//endregion

    //region SETTER
    public static void setCore(CoreComponent core) { ClientPlayerData.core = core; }
    public static void setEssence(EssenceComponent essence) { ClientPlayerData.essence = essence; }
    public static void setStatus(StatusComponent status) { ClientPlayerData.status = status; }
    public static void setPath(PathComponent path) { ClientPlayerData.path = path; }
    public static void setLifespan(LifespanComponent lifespan) { ClientPlayerData.lifespan = lifespan; }
    public static void setSoul(SoulComponent soul) { ClientPlayerData.soul = soul; }
//endregion

    public static void setAll(ModPlayerData data) {
        core = data.core();
        essence = data.essence();
        status = data.status();
        path = data.path();
        lifespan = data.lifespan();
        soul = data.soul();
    }

    public static void applyPathDeltas(List<PathDelta> deltas) {
        for (PathDelta delta : deltas) {
            path.setAttainment(delta.path(), delta.attainment());
            path.setMarks(delta.path(), delta.marks());
        }
        path.clearDirty();
    }
}
